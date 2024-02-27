package org.freedomtool.utils.nfc.model

import java.security.PublicKey

class EDocument {
    var docType: DocType? = null
    var personDetails: PersonDetails? = null
    var additionalPersonDetails: AdditionalPersonDetails? = null
    var isPassiveAuth = false
    var isActiveAuth = false
    var isChipAuth = false
    var isDocSignatureValid = false
    var docPublicKey: PublicKey? = null
}
