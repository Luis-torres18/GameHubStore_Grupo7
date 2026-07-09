package com.GameHubStore.auth_service.repository;

import com.GameHubStore.auth_service.model.entities.CuentaAcceso;
import com.GameHubStore.auth_service.model.entities.Rol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CuentaAccesoRepositoryTest {

    @Autowired
    private CuentaAccesoRepository cuentaAccesoRepository;

    @BeforeEach
    void setUp() {
        cuentaAccesoRepository.save(CuentaAcceso.builder()
                .email("ana@mail.com")
                .passwordHash("hash1")
                .rol(Rol.CLIENTE)
                .estado(true)
                .build());

        cuentaAccesoRepository.save(CuentaAcceso.builder()
                .email("admin@mail.com")
                .passwordHash("hash2")
                .rol(Rol.ADMINISTRADOR)
                .estado(true)
                .build());

        cuentaAccesoRepository.save(CuentaAcceso.builder()
                .email("inactivo@mail.com")
                .passwordHash("hash3")
                .rol(Rol.CLIENTE)
                .estado(false)
                .build());
    }

    @Test
    @DisplayName("existsByEmail debe retornar true si el email ya esta registrado")
    void existsByEmail_deberiaRetornarTrueSiExiste() {
        assertThat(cuentaAccesoRepository.existsByEmail("ana@mail.com")).isTrue();
        assertThat(cuentaAccesoRepository.existsByEmail("noexiste@mail.com")).isFalse();
    }

    @Test
    @DisplayName("findByEmail debe retornar la cuenta correspondiente")
    void findByEmail_deberiaRetornarCuenta() {
        Optional<CuentaAcceso> resultado = cuentaAccesoRepository.findByEmail("ana@mail.com");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getRol()).isEqualTo(Rol.CLIENTE);
    }

    @Test
    @DisplayName("findByEmail debe retornar vacio si no existe")
    void findByEmail_deberiaRetornarVacioSiNoExiste() {
        assertThat(cuentaAccesoRepository.findByEmail("noexiste@mail.com")).isEmpty();
    }

    @Test
    @DisplayName("findByRol debe retornar solo las cuentas del rol indicado")
    void findByRol_deberiaFiltrarPorRol() {
        List<CuentaAcceso> resultado = cuentaAccesoRepository.findByRol(Rol.CLIENTE);

        assertThat(resultado).hasSize(2)
                .allMatch(c -> c.getRol() == Rol.CLIENTE);
    }

    @Test
    @DisplayName("findByEstado debe retornar solo las cuentas activas o inactivas segun el filtro")
    void findByEstado_deberiaFiltrarPorEstado() {
        List<CuentaAcceso> activas = cuentaAccesoRepository.findByEstado(true);
        List<CuentaAcceso> inactivas = cuentaAccesoRepository.findByEstado(false);

        assertThat(activas).hasSize(2);
        assertThat(inactivas).hasSize(1);
        assertThat(inactivas.get(0).getEmail()).isEqualTo("inactivo@mail.com");
    }

    @Test
    @DisplayName("findByRolAndEstado debe combinar ambos filtros")
    void findByRolAndEstado_deberiaCombinarFiltros() {
        List<CuentaAcceso> resultado = cuentaAccesoRepository.findByRolAndEstado(Rol.CLIENTE, true);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEmail()).isEqualTo("ana@mail.com");
    }

    @Test
    @DisplayName("save debe asignar fechaCreacion automaticamente via @PrePersist")
    void save_deberiaAsignarFechaCreacionAutomaticamente() {
        CuentaAcceso guardada = cuentaAccesoRepository.save(CuentaAcceso.builder()
                .email("nueva@mail.com")
                .passwordHash("hash4")
                .rol(Rol.OPERADOR)
                .estado(true)
                .build());

        assertThat(guardada.getFechaCreacion()).isNotNull();
    }
}
