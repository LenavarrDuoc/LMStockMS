package cl.duoc.lmstockms.services;

import cl.duoc.lmstockms.clients.ToAPICatalogFeign;
import cl.duoc.lmstockms.dtos.InventarioInputDTO;
import cl.duoc.lmstockms.dtos.InventarioResponseDTO;
import cl.duoc.lmstockms.dtos.InventarioUpdateDTO;
import cl.duoc.lmstockms.dtos.ProductoDTO;
import cl.duoc.lmstockms.exceptions.IdExisteException;
import cl.duoc.lmstockms.exceptions.IdNoExisteException;
import cl.duoc.lmstockms.mappers.InventarioInputMapper;
import cl.duoc.lmstockms.mappers.InventarioResponseMapper;
import cl.duoc.lmstockms.mappers.InventarioUpdateMapper;
import cl.duoc.lmstockms.models.Inventario;
import cl.duoc.lmstockms.repositories.InventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service

public class InventarioService {
    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private InventarioInputMapper inventarioInputMapper;

    @Autowired
    private InventarioResponseMapper inventarioResponseMapper;

    @Autowired
    private InventarioUpdateMapper inventarioUpdateMapper;

    @Autowired
    ToAPICatalogFeign toAPICatalogFeign;

    //CREATE:
    @Transactional
    public InventarioResponseDTO save(InventarioInputDTO dto) {
        if (inventarioRepository.existsByProductoId(dto.getProductoId())) {
            throw new IdExisteException("ID de producto ya existe.");
        } else if (toAPICatalogFeign.obtener(dto.getProductoId()) == null) {
            throw new IdNoExisteException("ID de producto no existe.");
        }

        return inventarioResponseMapper.toDto(inventarioRepository.save(inventarioInputMapper.toEntity(dto)));

    }

    //READ:
    @Transactional(readOnly = true)
    public List<InventarioResponseDTO> findAll() {
        return inventarioRepository.findAll().stream().map(inventarioResponseMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public InventarioResponseDTO findById(Long id) {
        return inventarioResponseMapper.toDto(inventarioRepository.findById(id).orElseThrow(() -> new IdNoExisteException("ID de cliente no existe.")));
    }

    @Transactional(readOnly = true)
    public Boolean existsById(Long id) {
        return inventarioRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public InventarioResponseDTO findByProductoId(Long id) {
        Inventario ent = inventarioRepository.findByProductoId(id);
        if (ent == null) {
            throw new IdExisteException("ID de producto no existe.");
        }
        return inventarioResponseMapper.toDto(ent);
    }

    //UPDATE:
    @Transactional
    public InventarioResponseDTO update(InventarioUpdateDTO dto) {
        Inventario ent = inventarioRepository.findById(dto.getId()).orElseThrow(() -> new IdNoExisteException("ID de cliente no existe."));
        return inventarioResponseMapper.toDto(inventarioRepository.save(inventarioUpdateMapper.toEntity(ent, dto)));
    }

    //Delete:
    @Transactional
    public Boolean deleteById(Long id) {
        Boolean centinela = false;
        if (inventarioRepository.existsById(id)) {
            inventarioRepository.deleteById(id);
            centinela = true;
        } else {
            throw new IdNoExisteException("ID de cliente no existe.");
        }
        return centinela;
    }

}
