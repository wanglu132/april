package com.april.codec;

/**
 * Redis codec interface
 *
 * @author Nikita Koksharov
 *
 */
public interface Codec {


    /**
     * Returns object decoder used for any objects stored Redis structure except HMAP
     *
     * @return decoder
     */
    Decoder<Object> getDecoder();

    /**
     * Returns object encoder used for any objects stored Redis structure except HMAP
     *
     * @return encoder
     */
    Encoder getEncoder();

}
