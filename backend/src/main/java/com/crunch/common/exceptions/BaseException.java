package com.crunch.common.exceptions;

public abstract class BaseException extends RuntimeException{

        private static final long serialVersionUID = 3685410363359307585L;

        public BaseException() {
        }

        public BaseException(String message) {
            super(message);
        }

        public BaseException(Exception e) {
            super(e);
        }

        public BaseException(String message, Exception e) {
            super(message, e);
        }

        public abstract int getErrorCode();
    }
