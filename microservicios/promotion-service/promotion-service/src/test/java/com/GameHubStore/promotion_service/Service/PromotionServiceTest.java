package com.GameHubStore.promotion_service.Service;

import com.GameHubStore.promotion_service.service.PromotionServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.GameHubStore.promotion_service.client.CategoryClient;
import com.GameHubStore.promotion_service.client.ProductClient;
import com.GameHubStore.promotion_service.exception.InvalidPromotion;
import com.GameHubStore.promotion_service.model.dto.PromotionRequest;
import com.GameHubStore.promotion_service.model.dto.PromotionResponse;
import com.GameHubStore.promotion_service.model.entities.Promotion;
import com.GameHubStore.promotion_service.repository.PromotionRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.time.LocalDate;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromotionServiceTest {

    @Mock
    private PromotionRepository promotionRepository;
    @Mock
    private ProductClient productClient;
    @Mock
    private CategoryClient categoryClient;

    @InjectMocks
    private PromotionServiceImpl promotionService;

    private final Faker faker = new Faker();

    private Promotion promotion;
    private PromotionRequest request;


    @BeforeEach
    void setUp() {
        promotion = Promotion.builder()
                .id(1L)
                .code("SUMMER10")
                .type("PERCENTAGE")
                .discountAmount(10.0)
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(30))
                .minAmount(50.0)
                .maxUses(100)
                .currentUses(5)
                .productId(null)
                .categoryId(null)
                .isActive(true)
                .build();

        request = PromotionRequest.builder()
                .code("summer10")
                .type("percentage")
                .discountAmount(10.0)
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(30))
                .minAmount(50.0)
                .maxUses(100)
                .isActive(true)
                .productId(null)
                .categoryId(null)
                .build();
    }

    // ───────────────────────────────────────────
    // createPromotion
    // ───────────────────────────────────────────

    @Test
    @DisplayName("createPromotion - Crea promoción exitosamente sin producto ni categoría")
    void createPromotion_Success_WithoutProductOrCategory() {
        when(promotionRepository.existsByCode("SUMMER10")).thenReturn(false);
        when(promotionRepository.save(any(Promotion.class))).thenReturn(promotion);

        PromotionResponse response = promotionService.createPromotion(request);

        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("SUMMER10");
        assertThat(response.getIsActive()).isTrue();
        verify(promotionRepository, times(1)).save(any(Promotion.class));
    }

    @Test
    @DisplayName("createPromotion - El código se convierte a mayúsculas antes de guardar")
    void createPromotion_CodeIsUppercased_BeforeSaving() {
        when(promotionRepository.existsByCode("SUMMER10")).thenReturn(false);
        when(promotionRepository.save(any(Promotion.class))).thenReturn(promotion);

        promotionService.createPromotion(request);

        verify(promotionRepository).existsByCode("SUMMER10");
    }

    @Test
    @DisplayName("createPromotion - Lanza excepción si el código ya existe")
    void createPromotion_ThrowsInvalidPromotion_WhenCodeAlreadyExists() {
        when(promotionRepository.existsByCode("SUMMER10")).thenReturn(true);

        assertThatThrownBy(() -> promotionService.createPromotion(request))
                .isInstanceOf(InvalidPromotion.class)
                .hasMessageContaining("SUMMER10");

        verify(promotionRepository, never()).save(any());
    }

    @Test
    @DisplayName("createPromotion - Lanza excepción si fecha fin es anterior a fecha inicio")
    void createPromotion_ThrowsInvalidPromotion_WhenEndDateIsBeforeStartDate() {
        request.setStartDate(LocalDate.now().plusDays(10));
        request.setEndDate(LocalDate.now().minusDays(1));
        when(promotionRepository.existsByCode("SUMMER10")).thenReturn(false);
    }
}