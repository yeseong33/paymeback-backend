package com.paymeback.user.domain;

public sealed interface Password permits RawPassword, EncodedPassword {

}
