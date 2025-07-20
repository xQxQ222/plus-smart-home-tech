package ru.yandex.practicum.service;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

@Slf4j
@GrpcService
public class HubRouterController extends HubRouterControllerGrpc.HubRouterControllerImplBase {
    @Override
    public void handleDeviceAction(DeviceActionRequest request, StreamObserver<Empty> observer) {
        log.info("Получен запрос GRPC для DeviceAction: {}", request);
        try {
            observer.onNext(Empty.newBuilder().build());
            observer.onCompleted();
            log.info("Запрос GRPC DeviceAction успешно обработан");
        } catch (Exception e) {
            log.info("Ошибка при обработке запроса GRPC DeviceAction");
            observer.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }
}
