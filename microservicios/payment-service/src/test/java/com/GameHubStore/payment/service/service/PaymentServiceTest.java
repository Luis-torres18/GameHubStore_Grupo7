package com.GameHubStore.payment.service.service;

import com.GameHubStore.payment.client.InventoryClient;
import com.GameHubStore.payment.client.OrderClient;
import com.GameHubStore.payment.client.OrderDto;
import com.GameHubStore.payment.exception.PaymentNotFoundException;
import com.GameHubStore.payment.exception.PaymentValidationException;
import com.GameHubStore.payment.model.dto.PaymentRequest;
import com.GameHubStore.payment.model.dto.PaymentResponse;
import com.GameHubStore.payment.model.entities.Payment;
import com.GameHubStore.payment.repository.PaymentRepository;
import com.GameHubStore.payment.service.PaymentService;
import feign.FeignException;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderClient orderClient;

    @Mock
    private InventoryClient inventoryClient;

    @InjectMocks
    private PaymentService paymentService;

    private Payment paymentPrueba;
    private PaymentRequest requestPrueba;
    private OrderDto orderPrueba;
    private List<Payment> paymentList = new ArrayList<>();

    @BeforeEach
    public void setUp() {

        paymentList.clear();

        orderPrueba = new OrderDto();
        orderPrueba.setId(1L);
        orderPrueba.setTotal(50000.0);
        orderPrueba.setStatus("PENDING");

        requestPrueba = new PaymentRequest();
        requestPrueba.setOrdenId(1L);
        requestPrueba.setMonto(5000.0);
        requestPrueba.setMetodo("DEBITO");
        requestPrueba.setCodigoTransaccion("TXN-001");

        paymentPrueba = Payment.builder()
                .id(1L)
                .ordenId(1L)
                .monto(50000.0)
                .metodo("DEBITO")
                .estado("PENDING")
                .codigoTransaccion("TXN-001")
                .fecha(LocalDateTime.now())
                .build();
        Faker faker = new Faker(Locale.of("es", "CL"));
        for (int i = 0; i < 50; i++) {
            Payment payment = Payment.builder()
                    .id((long) (i + 2))
                    .ordenId((long) faker.number().numberBetween(1, 100))
                    .monto(faker.number().randomDouble(2, 10000, 200000))
                    .metodo(faker.options().option("DEBITO", "CREDITO", "TRANSFERENCIA"))
                    .estado(faker.options().option("PENDING", "APPROVED", "REJECTED"))
                    .codigoTransaccion("TXN" + faker.number().digits(6))
                    .fecha(LocalDateTime.now())
                    .build();
            paymentList.add(payment);
        }
    }

    @Test
    @DisplayName("Debe crear un pago válido cuando la orden existe y el monto coincide")
    public void shouldCreatePayment() {
        when(orderClient.getOrderById(1L)).thenReturn(List.of(orderPrueba));
        when(paymentRepository.existsByOrdenIdAndEstado(1L, "APPROVED")).thenReturn(false);
        when(paymentRepository.findByCodigoTransaccion("TXN-001")).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenReturn(paymentPrueba);

        PaymentResponse result = paymentService.createPayment(requestPrueba);

        assertThat(result).isNotNull();
        assertThat(result.getOrdenId()).isEqualTo(1L);
        assertThat(result.getMonto()).isEqualTo(50000.0);
        assertThat(result.getEstado()).isEqualTo("PENDING");
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(orderClient, times(1)).getOrderById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción si la orden no existe en order-service")
    public void shouldNotCreatePaymentWhenOrderNotFound() {
        when(orderClient.getOrderById(1L)).thenThrow(mock(FeignException.NotFound.class));

        assertThatThrownBy(() -> paymentService.createPayment(requestPrueba))
                .isInstanceOf(PaymentValidationException.class)
                .hasMessageContaining("No existe una orden con este ID: 1");
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si order-service no responde")
    public void shouldNotCreatePaymentWhenOrderServiceUnavailable() {
        when(orderClient.getOrderById(1L)).thenThrow(mock(FeignException.class));

        assertThatThrownBy(() -> paymentService.createPayment(requestPrueba))
                .isInstanceOf(PaymentValidationException.class)
                .hasMessageContaining("No se pudo validar la orden");
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el monto no coincide con el total de la orden")
    public void shouldNotCreatePaymentWhenAmountMismatch() {
        requestPrueba.setMonto(99999.0);
        when(orderClient.getOrderById(1L)).thenReturn(List.of(orderPrueba));

        assertThatThrownBy(() -> paymentService.createPayment(requestPrueba))
                .isInstanceOf(PaymentValidationException.class)
                .hasMessageContaining("no coincide con el total de la orden");
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si ya existe un pago aprobado para la orden")
    public void shouldNotCreatePaymentWhenDuplicateApproved() {
        when(orderClient.getOrderById(1L)).thenReturn(List.of(orderPrueba));
        when(paymentRepository.existsByOrdenIdAndEstado(1L, "APPROVED")).thenReturn(true);

        assertThatThrownBy(() -> paymentService.createPayment(requestPrueba))
                .isInstanceOf(PaymentValidationException.class)
                .hasMessageContaining("Ya existe un pago aprobado para la orden");
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el código de transacción ya existe")
    public void shouldNotCreatePaymentWhenDuplicateTransactionCode() {
        when(orderClient.getOrderById(1L)).thenReturn(List.of(orderPrueba));
        when(paymentRepository.existsByOrdenIdAndEstado(1L, "APPROVED")).thenReturn(false);
        when(paymentRepository.findByCodigoTransaccion("TXN-001")).thenReturn(Optional.of(paymentPrueba));

        assertThatThrownBy(() -> paymentService.createPayment(requestPrueba))
                .isInstanceOf(PaymentValidationException.class)
                .hasMessageContaining("El código de transacción ya existe");
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Debe listar todos los pagos correctamente")
    public void shouldGetAllPayments() {
        when(paymentRepository.findAll()).thenReturn(paymentList);

        List<PaymentResponse> result = paymentService.getAllPayments();

        assertThat(result).hasSize(50);
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener un pago por su ID")
    public void shouldGetPaymentById() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(paymentPrueba));

        PaymentResponse result = paymentService.getPaymentById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCodigoTransaccion()).isEqualTo("TXN-001");
        verify(paymentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar un pago con ID inexistente")
    public void shouldNotGetPaymentByIdWhenNotFound() {
        Long id = 9999L;
        when(paymentRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getPaymentById(id))
                .isInstanceOf(PaymentNotFoundException.class)
                .hasMessageContaining("Pago no encontrado con ID: " + id);
        verify(paymentRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe listar pagos por ordenId")
    public void shouldGetPaymentsByOrdenId() {
        when(paymentRepository.findByOrdenId(1L)).thenReturn(paymentList);

        List<PaymentResponse> result = paymentService.getPaymentsByOrdenId(1L);

        assertThat(result).hasSize(50);
        verify(paymentRepository, times(1)).findByOrdenId(1L);
    }

    @Test
    @DisplayName("Debe listar pagos por estado")
    public void shouldGetPaymentsByEstado() {
        when(paymentRepository.findByEstado("PENDING")).thenReturn(List.of(paymentPrueba));

        List<PaymentResponse> result = paymentService.getPaymentsByEstado("PENDING");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEstado()).isEqualTo("PENDING");
        verify(paymentRepository, times(1)).findByEstado("PENDING");
    }

    @Test
    @DisplayName("Debe aprobar el pago y confirmar stock en inventory-service")
    public void shouldUpdateEstadoToApproved() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(paymentPrueba));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(inventoryClient).confirmarPorOrdenId(1L);

        PaymentResponse result = paymentService.updateEstado(1L, "APPROVED");

        assertThat(result.getEstado()).isEqualTo("APPROVED");
        verify(inventoryClient, times(1)).confirmarPorOrdenId(1L);
        verify(inventoryClient, never()).liberarPorOrdenId(anyLong());
    }

    @Test
    @DisplayName("Debe rechazar el pago y liberar stock en inventory-service")
    public void shouldUpdateEstadoToRejected() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(paymentPrueba));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(inventoryClient).liberarPorOrdenId(1L);

        PaymentResponse result = paymentService.updateEstado(1L, "REJECTED");

        assertThat(result.getEstado()).isEqualTo("REJECTED");
        verify(inventoryClient, times(1)).liberarPorOrdenId(1L);
        verify(inventoryClient, never()).confirmarPorOrdenId(anyLong());
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar modificar un pago anulado")
    public void shouldNotUpdateEstadoWhenCancelled() {
        paymentPrueba.setEstado("CANCELLED");
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(paymentPrueba));

        assertThatThrownBy(() -> paymentService.updateEstado(1L, "APPROVED"))
                .isInstanceOf(PaymentValidationException.class)
                .hasMessageContaining("No se puede modificar un pago anulado");
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Debe anular un pago pendiente y liberar stock en inventory-service")
    public void shouldCancelPayment() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(paymentPrueba));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(inventoryClient).liberarPorOrdenId(1L);

        PaymentResponse result = paymentService.cancelPayment(1L);

        assertThat(result.getEstado()).isEqualTo("CANCELLED");
        verify(inventoryClient, times(1)).liberarPorOrdenId(1L);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar anular un pago aprobado")
    public void shouldNotCancelApprovedPayment() {
        paymentPrueba.setEstado("APPROVED");
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(paymentPrueba));

        assertThatThrownBy(() -> paymentService.cancelPayment(1L))
                .isInstanceOf(PaymentValidationException.class)
                .hasMessageContaining("No se puede anular un pago aprobado");
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(inventoryClient, never()).liberarPorOrdenId(anyLong());
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar anular un pago que ya está anulado")
    public void shouldNotCancelAlreadyCancelledPayment() {
        paymentPrueba.setEstado("CANCELLED");
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(paymentPrueba));

        assertThatThrownBy(() -> paymentService.cancelPayment(1L))
                .isInstanceOf(PaymentValidationException.class)
                .hasMessageContaining("El pago ya está anulado");
        verify(paymentRepository, never()).save(any(Payment.class));
    }
}


