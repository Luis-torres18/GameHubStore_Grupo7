package com.GameHubStore.review_service.Service;

import com.GameHubStore.review_service.client.OrderClient;
import com.GameHubStore.review_service.client.ProductClient;
import com.GameHubStore.review_service.client.UserClient;
import com.GameHubStore.review_service.client.dto.OrderResponse;
import com.GameHubStore.review_service.client.dto.ProductResponse;
import com.GameHubStore.review_service.client.dto.UserResponse;
import com.GameHubStore.review_service.exception.ReviewNotFoundException;
import com.GameHubStore.review_service.model.dto.ReviewRequest;
import com.GameHubStore.review_service.model.dto.ReviewResponse;
import com.GameHubStore.review_service.model.dto.UpdateReviewRequest;
import com.GameHubStore.review_service.model.entities.Review;
import com.GameHubStore.review_service.repository.ReviewRepository;
import com.GameHubStore.review_service.service.ReviewServiceImpl;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private ProductClient productClient;

    @Mock
    private OrderClient orderClient;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Review reviewPrueba;
    private ReviewRequest requestPrueba;
    private UserResponse usuarioPrueba;
    private ProductResponse productoPrueba;
    private OrderResponse ordenPrueba;
    private List<Review> reviewList;

    @BeforeEach
    public void setUp() {
        reviewList = new ArrayList<>();

        usuarioPrueba = new UserResponse();
        usuarioPrueba.setId(1L);
        usuarioPrueba.setNombre("Juan Pérez");
        usuarioPrueba.setEmail("juan@gamehub.cl");
        usuarioPrueba.setEstado(true);

        productoPrueba = new ProductResponse();
        productoPrueba.setId(1L);
        productoPrueba.setNombre("RTX 4090");
        productoPrueba.setEstado(true);

        ordenPrueba = new OrderResponse();
        ordenPrueba.setId(1L);
        ordenPrueba.setUserId(1L);
        ordenPrueba.setProductId(1L);
        ordenPrueba.setStatus("PAID");

        requestPrueba = new ReviewRequest();
        requestPrueba.setUserId(1L);
        requestPrueba.setProductId(1L);
        requestPrueba.setOrderId(1L);
        requestPrueba.setScore(5);
        requestPrueba.setComment("Excelente producto, muy recomendado");

        reviewPrueba = Review.builder()
                .id(1L)
                .userId(1L)
                .productId(1L)
                .orderId(1L)
                .score(5)
                .comment("Excelente producto, muy recomendado")
                .status(true)
                .date(LocalDateTime.now())
                .build();

        // 50 reseñas aleatorias con DataFaker
        Faker faker = new Faker(Locale.of("es", "CL"));
        for (int i = 0; i < 50; i++) {
            Review review = Review.builder()
                    .id((long) (i + 2))
                    .userId((long) faker.number().numberBetween(1, 20))
                    .productId((long) faker.number().numberBetween(1, 50))
                    .orderId((long) faker.number().numberBetween(1, 100))
                    .score(faker.number().numberBetween(1, 5))
                    .comment(faker.lorem().sentence())
                    .status(faker.bool().bool())
                    .date(LocalDateTime.now())
                    .build();
            reviewList.add(review);
        }

        System.out.println("==========================================");
        System.out.println("  Datos de prueba inicializados correctamente");
        System.out.println("  - Usuario: " + usuarioPrueba.getNombre() + " | Estado: " + usuarioPrueba.getEstado());
        System.out.println("  - Producto: " + productoPrueba.getNombre());
        System.out.println("  - Orden ID: " + ordenPrueba.getId() + " | Estado: " + ordenPrueba.getStatus());
        System.out.println("  - Reseñas aleatorias generadas: " + reviewList.size());
        System.out.println("==========================================");
    }

    // ══════════════════════════════════════════════════════════════
    // ─── createReview ─────────────────────────────────────────────
    // ══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Debe crear una reseña válida")
    public void shouldCreateReview() {
        System.out.println("\n[TEST] Debe crear una reseña válida");
        System.out.println("  → Entrada: userId=1, productId=1, orderId=1, score=5, comment='Excelente producto'");

        when(userClient.getUserById(1L)).thenReturn(usuarioPrueba);
        when(productClient.getProductById(1L)).thenReturn(List.of(productoPrueba));
        when(orderClient.getOrderById(1L)).thenReturn(List.of(ordenPrueba));
        when(reviewRepository.save(any(Review.class))).thenReturn(reviewPrueba);

        ReviewResponse result = reviewService.createReview(requestPrueba);

        System.out.println("  → Resultado: id=" + result.getId() + " | score=" + result.getScore() + " | status=" + result.getStatus());
        assertThat(result).isNotNull();
        assertThat(result.getScore()).isEqualTo(5);
        assertThat(result.getStatus()).isTrue();
        assertThat(result.getUserId()).isEqualTo(1L);
        verify(reviewRepository, times(1)).save(any(Review.class));
        System.out.println("  ✓ Reseña creada correctamente con score=5 y status=true");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario no existe")
    public void shouldNotCreateReviewWhenUserNotFound() {
        System.out.println("\n[TEST] Debe lanzar excepción si el usuario no existe");
        System.out.println("  → Simulando userClient.getUserById(1L) retorna null");

        when(userClient.getUserById(1L)).thenReturn(null);

        assertThatThrownBy(() -> reviewService.createReview(requestPrueba))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no existe");

        verify(reviewRepository, never()).save(any(Review.class));
        System.out.println("  ✓ Excepción IllegalArgumentException lanzada correctamente — usuario no existe");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario está inactivo")
    public void shouldNotCreateReviewWhenUserInactive() {
        System.out.println("\n[TEST] Debe lanzar excepción si el usuario está inactivo");
        usuarioPrueba.setEstado(false);
        System.out.println("  → Simulando usuario ID=1 con estado=false (inactivo)");

        when(userClient.getUserById(1L)).thenReturn(usuarioPrueba);

        assertThatThrownBy(() -> reviewService.createReview(requestPrueba))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inactivo");

        verify(reviewRepository, never()).save(any(Review.class));
        System.out.println("  ✓ Excepción IllegalArgumentException lanzada correctamente — usuario inactivo");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el producto no existe")
    public void shouldNotCreateReviewWhenProductNotFound() {
        System.out.println("\n[TEST] Debe lanzar excepción si el producto no existe");
        System.out.println("  → Simulando productClient.getProductById(1L) retorna lista vacía");

        when(userClient.getUserById(1L)).thenReturn(usuarioPrueba);
        when(productClient.getProductById(1L)).thenReturn(List.of());

        assertThatThrownBy(() -> reviewService.createReview(requestPrueba))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no existe");

        verify(reviewRepository, never()).save(any(Review.class));
        System.out.println("  ✓ Excepción IllegalArgumentException lanzada correctamente — producto no existe");
    }

    @Test
    @DisplayName("Debe lanzar excepción si la orden no existe")
    public void shouldNotCreateReviewWhenOrderNotFound() {
        System.out.println("\n[TEST] Debe lanzar excepción si la orden no existe");
        System.out.println("  → Simulando orderClient.getOrderById(1L) retorna lista vacía");

        when(userClient.getUserById(1L)).thenReturn(usuarioPrueba);
        when(productClient.getProductById(1L)).thenReturn(List.of(productoPrueba));
        when(orderClient.getOrderById(1L)).thenReturn(List.of());

        assertThatThrownBy(() -> reviewService.createReview(requestPrueba))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no existe");

        verify(reviewRepository, never()).save(any(Review.class));
        System.out.println("  ✓ Excepción IllegalArgumentException lanzada correctamente — orden no existe");
    }

    @Test
    @DisplayName("Debe lanzar excepción si la orden no corresponde al usuario y producto")
    public void shouldNotCreateReviewWhenOrderMismatch() {
        System.out.println("\n[TEST] Debe lanzar excepción si la orden no corresponde al usuario y producto");
        ordenPrueba.setUserId(99L);
        System.out.println("  → Simulando orden con userId=99 (distinto al usuario del request userId=1)");

        when(userClient.getUserById(1L)).thenReturn(usuarioPrueba);
        when(productClient.getProductById(1L)).thenReturn(List.of(productoPrueba));
        when(orderClient.getOrderById(1L)).thenReturn(List.of(ordenPrueba));

        assertThatThrownBy(() -> reviewService.createReview(requestPrueba))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no corresponde");

        verify(reviewRepository, never()).save(any(Review.class));
        System.out.println("  ✓ Excepción IllegalArgumentException lanzada correctamente — orden no corresponde");
    }

    // ══════════════════════════════════════════════════════════════
    // ─── GET ──────────────────────────────────────────────────────
    // ══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Debe listar todas las reseñas (50 con DataFaker)")
    public void shouldFindAllReviews() {
        System.out.println("\n[TEST] Debe listar todas las reseñas");
        System.out.println("  → Simulando repositorio con " + reviewList.size() + " reseñas generadas con DataFaker");

        when(reviewRepository.findAll()).thenReturn(reviewList);

        List<ReviewResponse> result = reviewService.findAll();

        System.out.println("  → Resultado: " + result.size() + " reseñas obtenidas");
        assertThat(result).hasSize(50);
        verify(reviewRepository, times(1)).findAll();
        System.out.println("  ✓ Lista retornada correctamente con 50 reseñas");
    }

    @Test
    @DisplayName("Debe obtener una reseña por su ID")
    public void shouldGetReviewById() {
        System.out.println("\n[TEST] Debe obtener una reseña por su ID");
        System.out.println("  → Buscando reseña con ID=1");

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(reviewPrueba));

        ReviewResponse result = reviewService.getReviewById(1L);

        System.out.println("  → Resultado: id=" + result.getId() + " | score=" + result.getScore() + " | comment='" + result.getComment() + "'");
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getScore()).isEqualTo(5);
        assertThat(result.getComment()).isEqualTo("Excelente producto, muy recomendado");
        verify(reviewRepository, times(1)).findById(1L);
        System.out.println("  ✓ Reseña encontrada correctamente");
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar una reseña inexistente")
    public void shouldNotGetReviewByIdWhenNotFound() {
        System.out.println("\n[TEST] Debe lanzar excepción al buscar una reseña inexistente");
        System.out.println("  → Buscando reseña con ID=9999 (no existe en el repositorio)");

        when(reviewRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.getReviewById(9999L))
                .isInstanceOf(ReviewNotFoundException.class)
                .hasMessageContaining("9999");

        verify(reviewRepository, times(1)).findById(9999L);
        System.out.println("  ✓ Excepción ReviewNotFoundException lanzada correctamente — ID 9999 no existe");
    }

    @Test
    @DisplayName("Debe listar reseñas por productId")
    public void shouldGetReviewsByProduct() {
        System.out.println("\n[TEST] Debe listar reseñas por productId");
        System.out.println("  → Buscando reseñas del producto ID=1");

        when(reviewRepository.findByProductId(1L)).thenReturn(List.of(reviewPrueba));

        List<ReviewResponse> result = reviewService.getReviewsByProduct(1L);

        System.out.println("  → Resultado: " + result.size() + " reseña(s) para productId=1 | score=" + result.get(0).getScore());
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProductId()).isEqualTo(1L);
        verify(reviewRepository, times(1)).findByProductId(1L);
        System.out.println("  ✓ Reseñas por producto retornadas correctamente");
    }

    @Test
    @DisplayName("Debe listar reseñas por userId")
    public void shouldGetReviewsByUser() {
        System.out.println("\n[TEST] Debe listar reseñas por userId");
        System.out.println("  → Buscando reseñas del usuario ID=1");

        when(reviewRepository.findByUserId(1L)).thenReturn(List.of(reviewPrueba));

        List<ReviewResponse> result = reviewService.getReviewsByUser(1L);

        System.out.println("  → Resultado: " + result.size() + " reseña(s) para userId=1");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(1L);
        verify(reviewRepository, times(1)).findByUserId(1L);
        System.out.println("  ✓ Reseñas por usuario retornadas correctamente");
    }

    // ══════════════════════════════════════════════════════════════
    // ─── updateReview ─────────────────────────────────────────────
    // ══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Debe actualizar el comentario y puntuación de una reseña")
    public void shouldUpdateReview() {
        System.out.println("\n[TEST] Debe actualizar el comentario y puntuación de una reseña");
        System.out.println("  → Actualizando reseña ID=1 | nuevo score=3 | nuevo comment='Producto regular'");

        UpdateReviewRequest updateRequest = new UpdateReviewRequest();
        updateRequest.setScore(3);
        updateRequest.setComment("Producto regular, cumple lo básico");

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(reviewPrueba));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        ReviewResponse result = reviewService.updateReview(1L, updateRequest);

        System.out.println("  → Resultado: score=" + result.getScore() + " | comment='" + result.getComment() + "'");
        assertThat(result.getScore()).isEqualTo(3);
        assertThat(result.getComment()).isEqualTo("Producto regular, cumple lo básico");
        verify(reviewRepository, times(1)).save(reviewPrueba);
        System.out.println("  ✓ Reseña actualizada correctamente");
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar una reseña inexistente")
    public void shouldNotUpdateReviewWhenNotFound() {
        System.out.println("\n[TEST] Debe lanzar excepción al actualizar una reseña inexistente");
        System.out.println("  → Intentando actualizar reseña con ID=9999 (no existe)");

        UpdateReviewRequest updateRequest = new UpdateReviewRequest();
        updateRequest.setScore(3);
        updateRequest.setComment("Comentario cualquiera");

        when(reviewRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.updateReview(9999L, updateRequest))
                .isInstanceOf(ReviewNotFoundException.class)
                .hasMessageContaining("9999");

        verify(reviewRepository, never()).save(any(Review.class));
        System.out.println("  ✓ Excepción ReviewNotFoundException lanzada correctamente");
    }

    // ══════════════════════════════════════════════════════════════
    // ─── moderateReview ───────────────────────────────────────────
    // ══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Debe moderar una reseña cambiando su estado a false")
    public void shouldModerateReview() {
        System.out.println("\n[TEST] Debe moderar una reseña cambiando su estado a false");
        System.out.println("  → Moderando reseña ID=1 | estado actual=true");

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(reviewPrueba));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        ReviewResponse result = reviewService.moderateReview(1L);

        System.out.println("  → Resultado: id=" + result.getId() + " | status=" + result.getStatus());
        assertThat(result.getStatus()).isFalse();
        verify(reviewRepository, times(1)).save(reviewPrueba);
        System.out.println("  ✓ Reseña moderada correctamente — status cambiado a false");
    }

    @Test
    @DisplayName("Debe lanzar excepción al moderar una reseña inexistente")
    public void shouldNotModerateReviewWhenNotFound() {
        System.out.println("\n[TEST] Debe lanzar excepción al moderar una reseña inexistente");
        System.out.println("  → Intentando moderar reseña con ID=9999 (no existe)");

        when(reviewRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.moderateReview(9999L))
                .isInstanceOf(ReviewNotFoundException.class)
                .hasMessageContaining("9999");

        verify(reviewRepository, never()).save(any(Review.class));
        System.out.println("  ✓ Excepción ReviewNotFoundException lanzada correctamente");
    }

    // ══════════════════════════════════════════════════════════════
    // ─── deleteReview ─────────────────────────────────────────────
    // ══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Debe eliminar una reseña existente")
    public void shouldDeleteReview() {
        System.out.println("\n[TEST] Debe eliminar una reseña existente");
        System.out.println("  → Eliminando reseña con ID=1");

        when(reviewRepository.existsById(1L)).thenReturn(true);
        doNothing().when(reviewRepository).deleteById(1L);

        String result = reviewService.deleteReview(1L);

        System.out.println("  → Resultado: '" + result + "'");
        assertThat(result).contains("1");
        assertThat(result).contains("eliminada");
        verify(reviewRepository, times(1)).deleteById(1L);
        System.out.println("  ✓ Reseña eliminada correctamente — mensaje de confirmación retornado");
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar una reseña inexistente")
    public void shouldNotDeleteReviewWhenNotFound() {
        System.out.println("\n[TEST] Debe lanzar excepción al eliminar una reseña inexistente");
        System.out.println("  → Intentando eliminar reseña con ID=9999 (no existe)");

        when(reviewRepository.existsById(9999L)).thenReturn(false);

        assertThatThrownBy(() -> reviewService.deleteReview(9999L))
                .isInstanceOf(ReviewNotFoundException.class)
                .hasMessageContaining("9999");

        verify(reviewRepository, never()).deleteById(any());
        System.out.println("  ✓ Excepción ReviewNotFoundException lanzada correctamente — no se puede eliminar lo que no existe");
    }
}