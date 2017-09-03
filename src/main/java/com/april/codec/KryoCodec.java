package com.april.codec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * 
 * @author Nikita Koksharov
 *
 */
public class KryoCodec implements Codec {

    public interface KryoPool {

        Kryo get();

        void yield(Kryo kryo);

    }

    public static class KryoPoolImpl implements KryoPool {

        private final Queue<Kryo> objects = new ConcurrentLinkedQueue<Kryo>();
        private final List<Class<?>> classes;
        private final ClassLoader classLoader;

        public KryoPoolImpl(List<Class<?>> classes, ClassLoader classLoader) {
            this.classes = classes;
            this.classLoader = classLoader;
        }

        public Kryo get() {
            Kryo kryo;
            if ((kryo = objects.poll()) == null) {
                kryo = createInstance();
            }
            return kryo;
        }

        public void yield(Kryo kryo) {
            objects.offer(kryo);
        }

        /**
         * Sub classes can customize the Kryo instance by overriding this method
         *
         * @return create Kryo instance
         */
        protected Kryo createInstance() {
            Kryo kryo = new Kryo();
            if (classLoader != null) {
                kryo.setClassLoader(classLoader);
            }
//            kryo.setReferences(false);
            for (Class<?> clazz : classes) {
                kryo.register(clazz);
            }
            return kryo;
        }

    }

    public class RedissonKryoCodecException extends RuntimeException {

        private static final long serialVersionUID = 9172336149805414947L;

        public RedissonKryoCodecException(Throwable cause) {
            super(cause.getMessage(), cause);
            setStackTrace(cause.getStackTrace());
        }
    }

    private final KryoPool kryoPool;

    private final Decoder<Object> decoder = new Decoder<Object>() {
        @Override
        public Object decode(byte[] buf) throws IOException {
            Kryo kryo = null;
            try {
                kryo = kryoPool.get();
                return kryo.readClassAndObject(new Input(new ByteArrayInputStream(buf)));
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new RedissonKryoCodecException(e);
            } finally {
                if (kryo != null) {
                    kryoPool.yield(kryo);
                }
            }
        }
    };

    private final Encoder encoder = new Encoder() {

        @Override
        public byte[] encode(Object in) throws IOException {
            Kryo kryo = null;
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Output output = new Output(baos);
                kryo = kryoPool.get();
                kryo.writeClassAndObject(output, in);
                output.close();
                return baos.toByteArray();
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new RedissonKryoCodecException(e);
            } finally {
                if (kryo != null) {
                    kryoPool.yield(kryo);
                }
            }
        }
    };

    public KryoCodec() {
        this(Collections.<Class<?>>emptyList());
    }

    public KryoCodec(ClassLoader classLoader) {
        this(Collections.<Class<?>>emptyList(), classLoader);
    }
    
    public KryoCodec(List<Class<?>> classes) {
        this(classes, null);
    }

    public KryoCodec(List<Class<?>> classes, ClassLoader classLoader) {
        this(new KryoPoolImpl(classes, classLoader));
    }

    public KryoCodec(KryoPool kryoPool) {
        this.kryoPool = kryoPool;
    }

    @Override
    public Decoder<Object> getDecoder() {
        return decoder;
    }

    @Override
    public Encoder getEncoder() {
        return encoder;
    }

}
