package citrea.swarm4j.core.meta.reflection;


import citrea.swarm4j.core.callback.OpRecipient;
import citrea.swarm4j.core.model.Syncable;
import citrea.swarm4j.core.model.annotation.SwarmOperationKind;
import citrea.swarm4j.core.meta.OperationMeta;
import citrea.swarm4j.core.spec.Spec;
import com.eclipsesource.json.JsonValue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 *
 * @author aleksisha
 *         Date: 22.08.2014
 *         Time: 16:04
 */
public class ReflectionOpMeta implements OperationMeta {

    private final Method method;
    private final SwarmOperationKind kind;
    private final SwarmMethodSignature signature;

    ReflectionOpMeta(Method method, SwarmOperationKind kind, SwarmMethodSignature signature) {
        this.method = method;
        this.kind = kind;
        this.signature = signature;
    }

    @Override
    public void invoke(Syncable object, Spec spec, JsonValue value, OpRecipient source) throws SwarmMethodInvocationException {
        try {
            switch (this.signature) {
                case NONE:
                    this.method.invoke(object);
                    break;
                case SPEC:
                    this.method.invoke(object, spec);
                    break;
                case SPEC_VALUE:
                    this.method.invoke(object, spec, value);
                    break;
                case SPEC_VALUE_SOURCE:
                    this.method.invoke(object, spec, value, source);
                    break;
                case SPEC_SOURCE:
                    this.method.invoke(object, spec, source);
                    break;
                case VALUE:
                    this.method.invoke(object, value);
                    break;
                case VALUE_SOURCE:
                    this.method.invoke(object, value, source);
                    break;
                case SOURCE:
                    this.method.invoke(object, source);
                    break;
                default:
                    throw new SwarmMethodInvocationException("Unsupported method signature: " + this.signature);
            }
        } catch (IllegalAccessException e) {
            throw new SwarmMethodInvocationException(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            Throwable ex = e.getTargetException();
            throw new SwarmMethodInvocationException(ex.getMessage(), ex);
        }
    }

    @Override
    public SwarmOperationKind getKind() {
        return kind;
    }

}
