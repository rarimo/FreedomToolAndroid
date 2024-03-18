package org.freedomtool.base

object BaseConfig {
    const val CREATE_IDENTITY_LINK =
        "https://kyc.freedomtool.org/integrations/identity-provider-service/v1/create-identity"

    const val GIST_DATA_LINK = "https://kyc.freedomtool.org/integrations/identity-provider-service/v1/gist-data"
    const val CLAIM_OFFER_LINK = "https://api.robotornot.rarimo.com/v1/{Did}/claims/{claim_id}/offer"

    const val SEND_REGISTRATION_LINK = "https://proofverification.freedomtool.org/integrations/proof-verification-relayer/v1/verify-proof"

    const val REGISTRATION_ADDRESS = "0x1d84cFd4839fE92dAe8E1F8F777010c08a60013C"
    const val PROPOSAL_ADDRESS = "0xF5Ca28acbBC7DFFfFFf714e1F306A803037Bdad2"
    const val CORE_LINK = "https://rpc-api.mainnet.rarimo.com"

    const val BLOCK_CHAIN_RPC_LINK = "https://arbitrum.freedomtool.org"

//    const val REGISTRATION_ADDRESS = "0xECB5371C727a664160c4F4360af77F0Fe4aeb7F9"
//    const val PROPOSAL_ADDRESS = "0xf41ceE234219D6cc3d90A6996dC3276aD378cfCF"
}