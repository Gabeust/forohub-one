package com.gabeust.forohub.service;

import com.gabeust.forohub.dto.NotificationEvent;
import com.gabeust.forohub.dto.ReactionDTO;
import com.gabeust.forohub.entity.Post;
import com.gabeust.forohub.entity.Reaction;
import com.gabeust.forohub.entity.User;
import com.gabeust.forohub.kafka.NotificationProducer;
import com.gabeust.forohub.mapper.ReactionMapper;
import com.gabeust.forohub.repository.IPostRepository;
import com.gabeust.forohub.repository.IReactionRepository;
import com.gabeust.forohub.repository.IUserRepository;
import com.gabeust.forohub.service.interf.IReactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Service
/**
 * Implementación del servicio para gestionar las reacciones en los posts del foro.
 *
 * Proporciona métodos para buscar, guardar, actualizar y eliminar reacciones,
 * vinculadas a usuarios y posts específicos.
 * Además, integra eventos Kafka para notificaciones cuando se crean o actualizan reacciones.
 */
public class ReactionServiceImpl implements IReactionService {

    private final IReactionRepository reactionRepository;
    private final ReactionMapper reactionMapper;
    private final IUserRepository userRepository;
    private final IPostRepository postRepository;
    private final NotificationProducer notificationProducer;

    public ReactionServiceImpl(IReactionRepository reactionRepository, ReactionMapper reactionMapper, IUserRepository userRepository, IPostRepository postRepository, NotificationProducer notificationProducer) {
        this.reactionRepository = reactionRepository;
        this.reactionMapper = reactionMapper;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.notificationProducer = notificationProducer;
    }
    /**
     * Devuelve todas las reacciones guardadas.
     *
     * @return lista de DTOs de reacciones
     */
    @Override
    public List<ReactionDTO> findAll() {
        return reactionRepository.findAll().stream()
                .map(reactionMapper::toDTO)
                .toList();
    }
    /**
     * Busca una reacción por su ID.
     *
     * @param id ID de la reacción
     * @return Optional con el DTO de la reacción si existe
     */
    @Override
    public Optional<ReactionDTO> findById(Long id) {
        return reactionRepository.findById(id)
                .map(reactionMapper::toDTO);
    }
    /**
     * Guarda o actualiza una reacción.
     *
     * Busca el usuario y el post asociados mediante nick y postId.
     * Si ya existe una reacción para ese usuario y post:
     * - Si el tipo de reacción es igual al nuevo, elimina la reacción (toggle off).
     * - Si es diferente, actualiza el tipo de reacción.
     * Si no existe, crea una nueva reacción.
     *
     * También envía un evento de notificación si el usuario que reacciona no es el autor del post.
     *
     * @param reactionDTO DTO con datos de la reacción a guardar o actualizar
     * @return DTO de la reacción guardada/actualizada, o null si se eliminó (toggle off)
     * @throws RuntimeException si no se encuentra usuario o post
     */
    @Transactional
    @Override
    public ReactionDTO save(ReactionDTO reactionDTO) {
        User author = userRepository.findByProfile_Nick(reactionDTO.nick())
                .orElseThrow(() -> new RuntimeException("User not found: " + reactionDTO.nick()));
        Post post = postRepository.findById(reactionDTO.postId())
                .orElseThrow(() -> new RuntimeException(("Post with ID: " + reactionDTO.postId() + " not found")));

        Optional<Reaction> existing = reactionRepository.findByUserIdAndPostId(author.getId(), post.getId());

        if (existing.isPresent()) {
            Reaction current = existing.get();
            if (current.getReactionType().equals(reactionDTO.reactionType())) {
                reactionRepository.delete(current);
                return null; // 💡 señal de que se eliminó
            } else {
                current.setReactionType(reactionDTO.reactionType());
                Reaction updated = reactionRepository.save(current);
                return reactionMapper.toDTO(updated);
            }
        }

        Reaction newReaction = new Reaction();
        newReaction.setUser(author);
        newReaction.setPost(post);
        newReaction.setReactionType(reactionDTO.reactionType());

        Reaction savedReaction  = reactionRepository.save(newReaction);

        if (!savedReaction.getUser().getId().equals(savedReaction.getPost().getAuthor().getId())) {
            NotificationEvent event = new NotificationEvent(
                    savedReaction.getPost().getAuthor().getId(),
                    "REACTION",
                    savedReaction.getUser().getProfile().getNick() + " reacted to your post.",
                    savedReaction.getPost().getId(),
                    null,
                    savedReaction.getId()
            );
            notificationProducer.sendNotification(event);
        }
        return reactionMapper.toDTO(savedReaction );
    }
    /**
     * Elimina una reacción por su ID.
     *
     * @param id ID de la reacción a eliminar
     */
    @Override
    public void deleteById(Long id) {
        reactionRepository.deleteById(id);
    }
}
