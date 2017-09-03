package com.april.codec;

import java.io.IOException;

public interface Decoder<R> {

    R decode(byte[] buf) throws IOException;

}
