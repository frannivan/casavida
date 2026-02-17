package com.casavida.backend.services;

import com.casavida.backend.dto.request.PagoRegistroDTO;
import com.casavida.backend.dto.request.PagoValidacionDTO;
import com.casavida.backend.dto.response.PagoResponseDTO;
import com.casavida.backend.entity.Pago;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for payment operations
 * Encapsulates business logic for payment management
 */
public interface PagoService {
    
    /**
     * Register a new payment
     * @param dto Payment registration data
     * @param authentication Current authenticated user
     * @return Created payment response
     * @throws com.casavida.backend.exception.EntityNotFoundException if contract not found
     * @throws com.casavida.backend.exception.BusinessException if business rules violated
     */
    PagoResponseDTO registrarPago(PagoRegistroDTO dto, Authentication authentication);
    
    /**
     * Attach receipt/comprobante to an existing payment
     * @param pagoId Payment ID
     * @param file Receipt file
     * @throws com.casavida.backend.exception.EntityNotFoundException if payment not found
     * @throws com.casavida.backend.exception.FileTooLargeException if file exceeds size limit
     * @throws com.casavida.backend.exception.InvalidFileTypeException if file type not allowed
     */
    void attachComprobante(Long pagoId, MultipartFile file);
    
    /**
     * Validate/approve a payment
     * @param pagoId Payment ID
     * @param dto Validation data (status, comments)
     * @param username User performing validation
     * @throws com.casavida.backend.exception.EntityNotFoundException if payment not found
     */
    void validarPago(Long pagoId, PagoValidacionDTO dto, String username);
    
    /**
     * Get all payments by contract
     * @param contratoId Contract ID
     * @return List of payments for the contract
     */
    List<Pago> getPagosByContrato(Long contratoId);
    
    /**
     * Get all payments (with role-based filtering)
     * @param authentication Current authenticated user
     * @return List of payments based on user role
     */
    List<Pago> getAllPagos(Authentication authentication);
    
    /**
     * Get payment receipt/comprobante
     * @param pagoId Payment ID
     * @return Payment entity with comprobante
     * @throws com.casavida.backend.exception.EntityNotFoundException if payment not found
     */
    Pago getComprobanteById(Long pagoId);
}
