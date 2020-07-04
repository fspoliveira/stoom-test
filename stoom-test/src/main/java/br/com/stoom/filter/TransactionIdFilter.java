package br.com.stoom.filter;

import br.com.stoom.exception.InvalidTransactionIdException;
import br.com.stoom.model.ErrorModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static br.com.stoom.util.Constants.TRANSACTION_ID_HEADER;
import static java.util.Optional.ofNullable;

@Data
@EqualsAndHashCode(callSuper = false)
@Component
@Slf4j
public class TransactionIdFilter extends OncePerRequestFilter {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String transactionId = getTransactionId(httpServletRequest);
            MDC.put(TRANSACTION_ID_HEADER, transactionId);
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } catch (InvalidTransactionIdException e) {
            log.error("Failed to retrieve transactionId {}", e.getMessage());
            httpServletResponse.setStatus(400);
            httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            httpServletResponse.getWriter()
                .write(objectMapper.writeValueAsString(ErrorModel.builder().message(e.getMessage()).build()));
        }

    }

    private String getTransactionId(HttpServletRequest httpServletRequest) {
        return ofNullable(httpServletRequest.getHeader(TRANSACTION_ID_HEADER)).orElseThrow(InvalidTransactionIdException::new);
    }
}
