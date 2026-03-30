package com.InfoRefineria.Service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SectorNotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    public SectorNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notificarCambio(String planta, String sector){
        String destino = "/topic/imagenes/" + planta + "/" + sector;
        messagingTemplate.convertAndSend(destino, Map.of(
                "planta", planta,
                "sector", sector,
                "timestamp", System.currentTimeMillis()
        ));
        System.out.println(">>> WebSocket notificado: " + destino);
    }
}
