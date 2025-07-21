package ru.yandex.practicum.service;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

@Slf4j
@RequiredArgsConstructor
@GrpcService
public class HubRouterController extends HubRouterControllerGrpc.HubRouterControllerImplBase {
    public void handleDeviceAction(DeviceActionRequest deviceActionRequest, StreamObserver<Empty> observer) {

        try {
            observer.onNext(Empty.newBuilder().build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }
}
