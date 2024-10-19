package pe.edu.cibertec.patitas_frontend_wc_a.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pe.edu.cibertec.patitas_frontend_wc_a.dto.RequestCloseEF;
import pe.edu.cibertec.patitas_frontend_wc_a.dto.ResponseCloseEF;

@FeignClient(name = "autenticacion", url = "http://localhost:8081/autenticacion")
public interface AutenticacionCliente {
  @PostMapping("/close_ef")
  ResponseEntity<ResponseCloseEF> closeEF(@RequestBody RequestCloseEF request);
}
