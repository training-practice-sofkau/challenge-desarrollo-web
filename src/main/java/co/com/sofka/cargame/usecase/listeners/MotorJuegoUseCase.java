package co.com.sofka.cargame.usecase.listeners;

import co.com.sofka.business.generic.UseCase;
import co.com.sofka.business.support.ResponseEvents;
import co.com.sofka.business.support.TriggeredEvent;
import co.com.sofka.cargame.domain.carril.values.CarrilId;
import co.com.sofka.cargame.domain.carro.values.CarroId;
import co.com.sofka.cargame.domain.juego.Juego;
import co.com.sofka.cargame.domain.juego.events.JuegoIniciado;
import co.com.sofka.cargame.domain.juego.values.JuegoId;
import co.com.sofka.cargame.usecase.services.CarrilCarroService;
import org.springframework.stereotype.Component;

@Component
public class MotorJuegoUseCase extends UseCase<TriggeredEvent<JuegoIniciado>, ResponseEvents> {

    @Override
    public void executeUseCase(TriggeredEvent<JuegoIniciado> triggeredEvent) {
        var event = triggeredEvent.getDomainEvent();
        var juegoId = JuegoId.of(event.aggregateRootId());
        var carrilCarroService = getService(CarrilCarroService.class).orElseThrow();
        var competidores = carrilCarroService.getCarrosSobreCarriles(juegoId);
        var juego = Juego.from(juegoId, retrieveEvents());

        if (!competidores.isEmpty()) {
            competidores.forEach(carroSobreCarril -> {
                juego.iniciarJuegoACompetidor(
                        CarroId.of(carroSobreCarril.getCarroId()),
                        CarrilId.of(carroSobreCarril.getCarrilId())
                );
            });
        }


        emit().onResponse(new ResponseEvents(juego.getUncommittedChanges()));
    }


}
