package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.Request;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping()
    public ResponseEntity<Object> createRequest(@RequestBody @Valid Request request,
                                      @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        log.info("Creating request {} from user №{}", request, requesterId);
        return requestClient.createRequest(request, requesterId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllRequestsByRequester(@RequestHeader("X-Sharer-User-Id") Long requesterId) {
        log.info("Get requests for requester №{}", requesterId);
        return requestClient.getAllRequestsByRequester(requesterId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                          @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Get requests for user №{}", userId);
        return requestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@PathVariable Long requestId,
                                           @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        log.info("Get request №{}", requestId);
        return requestClient.getRequest(requestId, requesterId);
    }
}
