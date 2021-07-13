package br.com.zup.edu.external.bacenPix

import br.com.zup.edu.proto.TipoChavePix

enum class TipoChaveBacen {
    CPF,
    PHONE,
    EMAIL,
    RANDOM {
        override fun toTipoChaveProtobuff(): TipoChavePix {
            return TipoChavePix.RANDOM_KEY
        }
    };

    open fun toTipoChaveProtobuff() = TipoChavePix.valueOf(this.toString())
}