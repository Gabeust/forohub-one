package com.gabeust.forohub.service;

import com.gabeust.forohub.dto.CommentDTO;
import com.gabeust.forohub.dto.NotificationEvent;
import com.gabeust.forohub.entity.Comment;
import com.gabeust.forohub.entity.Post;
import com.gabeust.forohub.entity.User;
import com.gabeust.forohub.kafka.NotificationProducer;
import com.gabeust.forohub.mapper.CommentMapper;
import com.gabeust.forohub.repository.IPostRepository;
import com.gabeust.forohub.repository.IUserRepository;
import com.gabeust.forohub.repository.iCommentrepository;
import com.gabeust.forohub.service.interf.ICommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
/**
 * Servicio para manejar la lógica de negocio relacionada con los comentarios (Comment).
 *
 * Proporciona métodos para crear, buscar y eliminar comentarios,
 * así como obtenerlos por post.
 *
 * Integra con Kafka para producir eventos de notificación cuando se crea un comentario.
 */
@Service
public class CommentServiceImpl implements ICommentService {
    private final iCommentrepository commentRepository;
    private final CommentMapper commentMapper;
    private final IPostRepository postRepository;
    private final IUserRepository userRepository;
    private final NotificationProducer notificationProducer;

    public CommentServiceImpl(iCommentrepository commnetRepository, CommentMapper commentMapper, IPostRepository postRepository, IUserRepository userRepository, NotificationProducer notificationProducer) {
        this.commentRepository = commnetRepository;
        this.commentMapper = commentMapper;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.notificationProducer = notificationProducer;
    }

    /**
     * Obtiene todos los comentarios existentes.
     *
     * @return Lista de CommentDTO
     */
    @Override
    public List<CommentDTO> findAll() {
        return commentRepository.findAll().stream()
                .map(commentMapper::toDTO).toList();
    }
    /**
     * Busca un comentario por su ID.
     *
     * @param id ID del comentario
     * @return Optional con el CommentDTO si existe
     */
    @Override
    public Optional<CommentDTO> findById(Long id) {
        return commentRepository.findById(id)
                .map(commentMapper::toDTO);
    }

    /**
     * Crea y guarda un nuevo comentario asociado a un post y a un autor.
     *
     * Si el autor del comentario es distinto al autor del post,
     * envía un evento de notificación a Kafka.
     *
     * @param commentDTO Datos del comentario a guardar (incluye postId y authorNick)
     * @return El CommentDTO guardado
     * @throws RuntimeException si el post o el usuario no existen
     */
    @Transactional
    @Override
    public CommentDTO save(CommentDTO commentDTO) {

        // Convertimos a entidad (sin author ni post)
        Comment comment = commentMapper.toEntity(commentDTO);

        // Buscamos el Post por ID desde el postDTO (suponiendo que postDTO NO es null)
        Post post = postRepository.findById(commentDTO.postId())
                .orElseThrow(() -> new RuntimeException("Post not found with id " + commentDTO.postId()));

        // Obtenemos el usuario actual (author) desde el contexto de seguridad
        User author = userRepository.findByProfile_Nick(commentDTO.authorNick())
                .orElseThrow(() -> new RuntimeException("User not found with username " + commentDTO.authorNick()));

        // Asignamos relaciones
        comment.setPost(post);
        comment.setAuthor(author);

        // Podemos asignar createdAt ahora o dejar que JPA lo haga automáticamente
        comment.setCreatedAt(LocalDateTime.now());

        // Guardamos la entidad
        Comment saved = commentRepository.save(comment);

        // Enviar evento Kafka solo si el autor del comentario no es el autor del post.
        if (!saved.getAuthor().getId().equals(saved.getPost().getAuthor().getId())) {
            NotificationEvent event = new NotificationEvent(
                    saved.getPost().getAuthor().getId(),
                    "COMMENT",
                    saved.getAuthor().getProfile().getNick() + " commented on your post.",
                    saved.getPost().getId(),
                    saved.getId(),
                    null
            );
            notificationProducer.sendNotification(event);
        }
        // Convertimos a DTO para devolver
        return commentMapper.toDTO(saved);
    }

    /**
     * Busca todos los comentarios de un post específico, ordenados por fecha de creación descendente.
     *
     * @param postId ID del post
     * @return Lista de CommentDTO
     */
    public List<CommentDTO> findByPostId(Long postId) {
        return commentRepository.findByPost_IdOrderByCreatedAtDesc(postId)
                .stream()
                .map(commentMapper::toDTO)
                .toList();
    }

    /**
     * Elimina un comentario por su ID.
     *
     * @param id ID del comentario a eliminar
     */

    @Override
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }
}
