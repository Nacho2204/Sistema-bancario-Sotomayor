package ar.edu.utn.frbb.tup.service;
import ar.edu.utn.frbb.tup.model.*;
import ar.edu.utn.frbb.tup.model.exception.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaNoSoportadaExeption;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import java.lang.String;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CuentaServiceTest {

    @Mock
    private CuentaDao cuentaDao;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private CuentaService cuentaService;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //Generar casos de test para darDeAltaCuenta

    //    1 - cuenta existente
    @Test
    public void testCuentaExistente() throws CuentaAlreadyExistsException{
        Cuenta cuenta = new Cuenta();
        long dniTitular = 123456;
        cuenta.setNumeroCuenta(123456);

        when(cuentaDao.find(cuenta.getNumeroCuenta())).thenReturn(cuenta);
        //Si encuentra la cuenta, lanza la excepcion
        assertThrows(CuentaAlreadyExistsException.class, () -> cuentaService.darDeAltaCuenta(cuenta, dniTitular));
    }

    //    2 - cuenta no soportada
    @Test
    public void testCuentaNoSoportada() throws TipoCuentaNoSoportadaExeption, CuentaAlreadyExistsException, TipoCuentaAlreadyExistsException {
        Cuenta cuentaNoSoportada = new Cuenta();
        cuentaNoSoportada.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);
        cuentaNoSoportada.setMoneda(TipoMoneda.DOLARES);
        cuentaNoSoportada.setNumeroCuenta(654123);


        when(cuentaDao.find(cuentaNoSoportada.getNumeroCuenta())).thenReturn(null);
        assertThrows(TipoCuentaNoSoportadaExeption.class, () -> cuentaService.darDeAltaCuenta(cuentaNoSoportada, 12365412));
        verify(cuentaDao, never()).save(any(Cuenta.class));
    }


    //    3 - cliente ya tiene cuenta de ese tipo
    @Test
    public void testClienteYaTieneCuentaCAP() throws TipoCuentaAlreadyExistsException, CuentaAlreadyExistsException, TipoCuentaNoSoportadaExeption{

            long dniTitular = 123;
            Cuenta cuenta = new Cuenta()
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(500000)
                .setTipoCuenta(TipoCuenta.CAJA_AHORRO);
            cuenta.setNumeroCuenta(112312);

            clienteService.agregarCuenta(cuenta, dniTitular);

            Cuenta cuenta2 = new Cuenta()
                    .setMoneda(TipoMoneda.PESOS)
                    .setBalance(500000)
                    .setTipoCuenta(TipoCuenta.CAJA_AHORRO);
            cuenta2.setNumeroCuenta(321);

        doThrow(TipoCuentaAlreadyExistsException.class).when(clienteService).agregarCuenta(cuenta2, dniTitular);

        assertThrows(TipoCuentaAlreadyExistsException.class, () -> clienteService.agregarCuenta(cuenta2, dniTitular));
    }

    @Test
    public void testClienteYaTieneCuentaCCP() throws TipoCuentaAlreadyExistsException, CuentaAlreadyExistsException, TipoCuentaNoSoportadaExeption{
        Cuenta cuenta = new Cuenta();
        long dniTitular = 123456;
        cuenta.setNumeroCuenta(654123);
        cuenta.setMoneda(TipoMoneda.PESOS);
        cuenta.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);

       clienteService.agregarCuenta(cuenta, dniTitular);

        Cuenta cuenta2 = new Cuenta();
        cuenta2.setNumeroCuenta(987456);
        cuenta2.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);
        cuenta2.setMoneda(TipoMoneda.PESOS);

        doThrow(TipoCuentaAlreadyExistsException.class).when(clienteService).agregarCuenta(cuenta2, dniTitular);

        assertThrows(TipoCuentaAlreadyExistsException.class, () -> clienteService.agregarCuenta(cuenta2, dniTitular));
    }

    @Test
    public void testClienteYaTieneCuentaCAD() throws TipoCuentaAlreadyExistsException, CuentaAlreadyExistsException, TipoCuentaNoSoportadaExeption {
        Cuenta cuenta = new Cuenta();
        long dniTitular = 123456;
        cuenta.setNumeroCuenta(654123);
        cuenta.setMoneda(TipoMoneda.DOLARES);
        cuenta.setTipoCuenta(TipoCuenta.CAJA_AHORRO);

        clienteService.agregarCuenta(cuenta, dniTitular);

        Cuenta cuenta2 = new Cuenta();
        cuenta2.setNumeroCuenta(987456);
        cuenta2.setTipoCuenta(TipoCuenta.CAJA_AHORRO);
        cuenta2.setMoneda(TipoMoneda.DOLARES);

        doThrow(TipoCuentaAlreadyExistsException.class).when(clienteService).agregarCuenta(cuenta2, dniTitular);

        assertThrows(TipoCuentaAlreadyExistsException.class, () -> clienteService.agregarCuenta(cuenta2, dniTitular));
    }



    //    4 - cuenta creada exitosamente
    @Test
    public void testCuentaSucces() throws CuentaAlreadyExistsException, TipoCuentaAlreadyExistsException, TipoCuentaNoSoportadaExeption{
        Cuenta cuenta = new Cuenta();
        long dniTitular = 123456;
        cuenta.setNumeroCuenta(654123);
        cuenta.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);
        cuenta.setMoneda(TipoMoneda.PESOS);
        
        when(cuentaDao.find(cuenta.getNumeroCuenta())).thenReturn(null);
        cuentaService.darDeAltaCuenta(cuenta, dniTitular);

        verify(cuentaDao, times(1)).save(cuenta);
        assertEquals(654123, cuenta.getNumeroCuenta());
    }
}