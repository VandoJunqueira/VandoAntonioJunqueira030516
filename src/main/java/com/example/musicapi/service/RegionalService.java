package com.example.musicapi.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.musicapi.dto.RegionalDTO;
import com.example.musicapi.model.Regional;
import com.example.musicapi.repository.RegionalRepository;

@Service
public class RegionalService {

    private final RegionalRepository regionalRepository;
    private final RestTemplate restTemplate;

    public RegionalService(RegionalRepository regionalRepository) {
        this.regionalRepository = regionalRepository;
        this.restTemplate = new RestTemplate();
    }

    public List<Regional> findAllSynced() {

        RegionalDTO[] externalRegionalsArray;
        try {
            externalRegionalsArray = restTemplate.getForObject("https://integrador-argus-api.geia.vip/v1/regionais", RegionalDTO[].class);
        } catch (Exception e) {
            externalRegionalsArray = new RegionalDTO[0];
        }

        List<RegionalDTO> externalList = externalRegionalsArray != null ? List.of(externalRegionalsArray) : Collections.emptyList();

        List<Regional> localActiveList = regionalRepository.findByAtivoTrue();

        Map<Integer, Regional> localMapByExternalId = localActiveList.stream()
                .filter(r -> r.getExternalId() != null)
                .collect(Collectors.toMap(Regional::getExternalId, Function.identity()));
        Map<Integer, RegionalDTO> externalMapById = externalList.stream()
                .collect(Collectors.toMap(RegionalDTO::getId, Function.identity()));

        for (RegionalDTO ext : externalList) {
            if (!localMapByExternalId.containsKey(ext.getId())) {
                Regional newRegional = new Regional(ext.getNome(), true, ext.getId());
                regionalRepository.save(newRegional);
            } else {
                Regional local = localMapByExternalId.get(ext.getId());
                if (!local.getNome().equals(ext.getNome())) {
                    local.setAtivo(false);
                    regionalRepository.save(local);
                    Regional newRegional = new Regional(ext.getNome(), true, ext.getId());
                    regionalRepository.save(newRegional);
                }
            }
        }

        for (Regional local : localActiveList) {
            if (local.getExternalId() != null && !externalMapById.containsKey(local.getExternalId())) {
                local.setAtivo(false);
                regionalRepository.save(local);
            }
        }

        return regionalRepository.findByAtivoTrue();
    }
}
