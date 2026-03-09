package br.com.his.access.context;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

@Component
public class UnidadeContext {

    private static final String SESSION_KEY = "unidade_atual_id";

    public Optional<Long> getUnidadeAtual() {
        HttpSession session = getSession(false);
        if (session == null) {
            return Optional.empty();
        }
        Object value = session.getAttribute(SESSION_KEY);
        if (value instanceof Long id) {
            return Optional.of(id);
        }
        if (value instanceof Number number) {
            return Optional.of(number.longValue());
        }
        return Optional.empty();
    }

    public void setUnidadeAtual(Long id) {
        if (id == null) {
            clear();
            return;
        }
        HttpSession session = getSession(true);
        if (session != null) {
            session.setAttribute(SESSION_KEY, id);
        }
    }

    public void clear() {
        HttpSession session = getSession(false);
        if (session != null) {
            session.removeAttribute(SESSION_KEY);
        }
    }

    private HttpSession getSession(boolean create) {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes servletAttrs) {
            return servletAttrs.getRequest().getSession(create);
        }
        return null;
    }
}
