package com.example.musicapi.service;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import com.example.musicapi.dto.RegionalDTO;
import com.example.musicapi.model.Regional;
import com.example.musicapi.repository.RegionalRepository;

@ExtendWith(MockitoExtension.class)
class RegionalServiceTest {

    @Mock
    private RegionalRepository regionalRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RegionalService regionalService;

    /**
     * Testa a sincronização bem-sucedida, onde dados locais são atualizados com
     * base na resposta da API externa.
     */
    @Test
    void findAllSynced_ShouldSyncAndReturnRegionals() {
        // Arrange
        RegionalDTO externalRegional = new RegionalDTO();
        externalRegional.setId(1);
        externalRegional.setNome("Nordeste");
        RegionalDTO[] externalArray = new RegionalDTO[]{externalRegional};

        when(restTemplate.getForObject(ArgumentMatchers.anyString(), ArgumentMatchers.eq(RegionalDTO[].class)))
                .thenReturn(externalArray);

        when(regionalRepository.findByAtivoTrue()).thenReturn(Collections.emptyList()) // Primeira chamada (estado inicial local)
                .thenReturn(List.of(new Regional("Nordeste", true, 1))); // Segunda chamada (após sync)

        // Act
        List<Regional> result = regionalService.findAllSynced();

        // Assert
        verify(regionalRepository, atLeastOnce()).save(ArgumentMatchers.any(Regional.class));
        assertFalse(result.isEmpty());
        assertEquals("Nordeste", result.get(0).getNome());
    }

    /**
     * Verifica comportamento quando a API externa falha, garantindo que nenhuma
     * sincronização indevida ocorra.
     */
    @Test
    void findAllSynced_ShouldHandleApiFailure() {
        // Arrange
        when(restTemplate.getForObject(ArgumentMatchers.anyString(), ArgumentMatchers.eq(RegionalDTO[].class)))
                .thenThrow(new RuntimeException("API error"));

        when(regionalRepository.findByAtivoTrue()).thenReturn(Collections.emptyList());

        // Act
        List<Regional> result = regionalService.findAllSynced();

        // Assert
        assertTrue(result.isEmpty());
        // Não deve salvar nada se a API falhar e não tiver locais
        verify(regionalRepository, never()).save(ArgumentMatchers.any(Regional.class));
    }
}
