package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.TipoCuenta;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaNoSoportadaExeption;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ar.edu.utn.frbb.tup.model.TipoMoneda;
import java.util.HashMap;
import java.util.Map;


@Component
public class CuentaService {
    CuentaDao cuentaDao = new CuentaDao();

    @Autowired
    ClienteService clienteService;


    public void darDeAltaCuenta(Cuenta cuenta, long dniTitular) throws CuentaAlreadyExistsException, TipoCuentaAlreadyExistsException, TipoCuentaNoSoportadaExeption {
        if(cuentaDao.find(cuenta.getNumeroCuenta()) != null) {
            throw new CuentaAlreadyExistsException("La cuenta " + cuenta.getNumeroCuenta() + " ya existe.");
        }

        //Chequear cuentas soportadas por el banco CA$ CC$ CAU$S
        // if (!tipoCuentaEstaSoportada(cuenta)) {...}
        /*Agregue un tipo de cuenta no soportada para que haya una posibilidad de que un tipo de cuenta no sea valido,
        ya que el usuario solo puede elegir las que si estan soportadas. */

        if (!isTipoCuentaSupported(cuenta)) {
            throw new TipoCuentaNoSoportadaExeption("El tipo de cuenta " + cuenta.getTipoCuenta() + " no esta soportado.");
        }

        clienteService.agregarCuenta(cuenta, dniTitular);
        cuentaDao.save(cuenta);
    }

    public Cuenta find(long id) {
        return cuentaDao.find(id);
    }

    private boolean isTipoCuentaSupported (Cuenta cuenta) {
        if (cuenta.getTipoCuenta().equals(TipoCuenta.CUENTA_CORRIENTE) && cuenta.getMoneda().equals(TipoMoneda.DOLARES)) {
            return false;
        }
        return true;
    }
}
