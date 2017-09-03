package com.april.codec;

import java.io.IOException;

public interface Encoder {

    byte[] encode(Object in) throws IOException;

}
