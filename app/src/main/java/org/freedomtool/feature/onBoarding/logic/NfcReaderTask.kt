package org.freedomtool.feature.onBoarding.logic

import android.content.Context
import android.nfc.tech.IsoDep
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import io.reactivex.subjects.PublishSubject
import net.sf.scuba.smartcards.CardService
import org.bouncycastle.asn1.cms.SignedData
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.freedomtool.logic.persistance.SecureSharedPrefs
import org.freedomtool.utils.nfc.model.AdditionalPersonDetails
import org.freedomtool.utils.nfc.model.DocType
import org.freedomtool.utils.nfc.model.EDocument
import org.freedomtool.utils.nfc.model.PersonDetails
import org.freedomtool.utils.nfc.DateUtil
import org.freedomtool.utils.nfc.model.Image
import org.freedomtool.utils.nfc.ImageUtil
import org.freedomtool.utils.nfc.SecurityUtil
import org.freedomtool.utils.nfc.StringUtil
import org.jmrtd.BACKeySpec
import org.jmrtd.PassportService
import org.jmrtd.Util
import org.jmrtd.lds.CardSecurityFile
import org.jmrtd.lds.ChipAuthenticationInfo
import org.jmrtd.lds.ChipAuthenticationPublicKeyInfo
import org.jmrtd.lds.PACEInfo
import org.jmrtd.lds.SODFile
import org.jmrtd.lds.icao.DG14File
import org.jmrtd.lds.icao.DG15File
import org.jmrtd.lds.icao.DG1File
import org.jmrtd.lds.icao.DG2File
import org.jmrtd.lds.icao.DG5File
import org.jmrtd.lds.icao.DG7File
import org.jmrtd.lds.iso19794.FaceImageInfo
import org.jmrtd.protocol.EACCAResult
import java.io.InputStream
import java.security.MessageDigest
import java.security.SecureRandom
import java.security.Security
import java.util.Arrays

@OptIn(ExperimentalStdlibApi::class)
class NfcReaderTask (
    private val isoDep: IsoDep,
    private val bacKey: BACKeySpec,
    val context: Context
) :
    AsyncTask<Void?, String?, Exception?>() {
    private var eDocument: EDocument = EDocument()
    private var docType: DocType = DocType.OTHER
    private var personDetails: PersonDetails = PersonDetails()
    private var additionalPersonDetails: AdditionalPersonDetails = AdditionalPersonDetails()

    private val resultSubject = PublishSubject.create<EDocument>()

    fun getResultSubject(): io.reactivex.Observable<EDocument> {
        return resultSubject
    }

    fun cropByteArray(inputByteArray: ByteArray, endNumber: Int): ByteArray {
        // Make sure endNumber is within bounds
        val endIndex = if (endNumber > inputByteArray.size) inputByteArray.size else endNumber

        // Use copyOfRange to crop the ByteArray
        return inputByteArray.copyOfRange(0, endIndex)
    }


    override fun onProgressUpdate(vararg values: String?) {
        super.onProgressUpdate(*values)
        //loadingInfo.setText(values[0])
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun doInBackground(vararg params: Void?): Exception? {
        try {
            val cardService = CardService.getInstance(isoDep)
            cardService.open()
            val service = PassportService(
                cardService,
                PassportService.NORMAL_MAX_TRANCEIVE_LENGTH,
                PassportService.DEFAULT_MAX_BLOCKSIZE,
                true,
                false
            )
            service.open()
            var paceSucceeded = false
            try {
                val cardSecurityFile =
                    CardSecurityFile(service.getInputStream(PassportService.EF_CARD_SECURITY))
                val securityInfoCollection = cardSecurityFile.securityInfos
                for (securityInfo in securityInfoCollection) {
                    if (securityInfo is PACEInfo) {
                        val paceInfo = securityInfo
                        service.doPACE(
                            bacKey,
                            paceInfo.objectIdentifier,
                            PACEInfo.toParameterSpec(paceInfo.parameterId),
                            null
                        )
                        paceSucceeded = true
                    }
                }
            } catch (e: Exception) {
                Log.w("Error", e)
            }
            service.sendSelectApplet(paceSucceeded)
            if (!paceSucceeded) {
                try {
                    service.getInputStream(PassportService.EF_COM).read()
                } catch (e: Exception) {
                    e.printStackTrace()
                    service.doBAC(bacKey)
                }
            }

            var hashesMatched = true
            var activeAuth = true
            var chipAuth = true
            publishProgress("Reading sod file")
            val sodIn1 = service.getInputStream(PassportService.EF_SOD)

            var byteArray = ByteArray(1024*1024)

            val byteLen = sodIn1.read(byteArray)

            SecureSharedPrefs.saveSOD(context, cropByteArray(byteArray, byteLen).toHexString())


            val sodIn = service.getInputStream(PassportService.EF_SOD)

            val sodFile = SODFile(sodIn)

            sodFile.dataGroupHashes.entries.forEach { (key, value) ->
                Log.d("", "Data group: $key hash value: ${StringUtil.byteArrayToHex(value)}")
            }

            var digestAlgorithm = sodFile.digestAlgorithm
            Log.d(
                "",
                "Digest Algorithm: $digestAlgorithm"
            )
            val docSigningCert = sodFile.docSigningCertificate
            val docSigningCerts = sodFile.docSigningCertificates
            val pemFile: String = SecurityUtil.convertToPem(docSigningCert)
            Log.d(
                "",
                "Document Signer Certificate: $docSigningCert"
            )
            Log.d(
                "",
                "Document Signer Certificate Pem : $pemFile"
            )
            val digestEncryptionAlgorithm = sodFile.digestEncryptionAlgorithm
            val digest: MessageDigest
            publishProgress("Loading digest algorithm")
            digest = if (Security.getAlgorithms("MessageDigest").contains(digestAlgorithm)) {
                MessageDigest.getInstance(digestAlgorithm)
            } else {
                MessageDigest.getInstance(digestAlgorithm, BouncyCastleProvider())
            }
            publishProgress("Reading Personal Details")



            // -- Personal Details -- //
            val dg1In = service.getInputStream(PassportService.EF_DG1)
            val dg1File = DG1File(dg1In)
            var encodedDg1File = String(dg1File.encoded)
            val mrzInfo = dg1File.mrzInfo
            personDetails.name =
                mrzInfo.secondaryIdentifier.replace("<", " ").trim { it <= ' ' }
            personDetails.surname =
                mrzInfo.primaryIdentifier.replace("<", " ").trim { it <= ' ' }
            personDetails.personalNumber = mrzInfo.personalNumber;
            personDetails.gender = mrzInfo.gender.toString();
            personDetails.birthDate = DateUtil.convertFromMrzDate(mrzInfo.dateOfBirth);
            personDetails.expiryDate = DateUtil.convertFromMrzDate(mrzInfo.dateOfExpiry);
            personDetails.serialNumber = mrzInfo.documentNumber;
            personDetails.nationality = mrzInfo.nationality;
            personDetails.issuerAuthority = mrzInfo.issuingState;

            SecureSharedPrefs.saveDG1(context, encodedDg1File)

            if ("I" == mrzInfo.documentCode) {
                docType = DocType.ID_CARD
                encodedDg1File =
                    StringUtil.fixPersonalNumberMrzData(encodedDg1File, mrzInfo.personalNumber)
            } else if ("P" == mrzInfo.documentCode) {
                docType = DocType.PASSPORT
            }
            val dg1StoredHash = sodFile.dataGroupHashes[1]
            val dg1ComputedHash = digest.digest(encodedDg1File.toByteArray())
            Log.d(
                "",
                "DG1 Stored Hash: " + StringUtil.byteArrayToHex(dg1StoredHash!!)
            )
            Log.d(
                "",
                "DG1 Computed Hash: " + StringUtil.byteArrayToHex(dg1ComputedHash)
            )
            if (Arrays.equals(dg1StoredHash, dg1ComputedHash)) {
                Log.d("", "DG1 Hashes are matched")
            } else {
                hashesMatched = false
            }
            publishProgress("Reading Face Image")

            // -- Face Image -- //
            val dg2In = service.getInputStream(PassportService.EF_DG2)
            val dg2File = DG2File(dg2In)
            publishProgress("Decoding Face Image")
            val dg2StoredHash = sodFile.dataGroupHashes[2]
            val dg2ComputedHash = digest.digest(dg2File.encoded)
            Log.d(
                "",
                "DG2 Stored Hash: " + StringUtil.byteArrayToHex(dg2StoredHash!!)
            )
            Log.d(
                "",
                "DG2 Computed Hash: " + StringUtil.byteArrayToHex(dg2ComputedHash)
            )
            if (Arrays.equals(dg2StoredHash, dg2ComputedHash)) {
                Log.d("", "DG2 Hashes are matched")
            } else {
                hashesMatched = false
            }
            val faceInfos = dg2File.faceInfos
            val allFaceImageInfos: MutableList<FaceImageInfo> = ArrayList()
            for (faceInfo in faceInfos) {
                allFaceImageInfos.addAll(faceInfo.faceImageInfos)
            }
            if (!allFaceImageInfos.isEmpty()) {
                val faceImageInfo = allFaceImageInfos.iterator().next()
                val image: Image = ImageUtil.getImage(context, faceImageInfo)
                personDetails.faceImage = image.bitmapImage
                personDetails.faceImageBase64 = image.base64Image
            }
            publishProgress("Reading Portrait Picture")

            // -- Portrait Picture -- //
            try {
                val dg5In = service.getInputStream(PassportService.EF_DG5)
                val dg5File = DG5File(dg5In)

                val dg5StoredHash = sodFile.dataGroupHashes[5]
                val dg5ComputedHash = digest.digest(dg5File.encoded)
                Log.d(
                    "",
                    "DG5 Stored Hash: " + StringUtil.byteArrayToHex(dg5StoredHash!!)
                )
                Log.d(
                    "",
                    "DG5 Computed Hash: " + StringUtil.byteArrayToHex(dg5ComputedHash)
                )
                if (Arrays.equals(dg5StoredHash, dg5ComputedHash)) {
                    Log.d(
                        "",
                        "DG5 Hashes are matched"
                    )
                } else {
                    hashesMatched = false
                }
                val displayedImageInfos = dg5File.images
                if (!displayedImageInfos.isEmpty()) {
                    val displayedImageInfo = displayedImageInfos.iterator().next()
                    val image: Image = ImageUtil.getImage(context, displayedImageInfo)
                    personDetails.portraitImage = image.bitmapImage
                    personDetails.portraitImageBase64 = image.base64Image
                }
            } catch (e: Exception) {
                Log.w("", e)
            }
            publishProgress("Reading Signature")

            // -- Signature (if exist) -- //
            try {
                val dg7In = service.getInputStream(PassportService.EF_DG7)
                val dg7File = DG7File(dg7In)
                val dg7StoredHash = sodFile.dataGroupHashes[7]
                val dg7ComputedHash = digest.digest(dg7File.encoded)
                Log.d(
                    "",
                    "DG7 Stored Hash: " + StringUtil.byteArrayToHex(dg7StoredHash!!)
                )
                Log.d(
                    "",
                    "DG7 Computed Hash: " + StringUtil.byteArrayToHex(dg7ComputedHash)
                )
                if (Arrays.equals(dg7StoredHash, dg7ComputedHash)) {
                    Log.d(
                        "",
                        "DG7 Hashes are matched"
                    )
                } else {
                    hashesMatched = false
                }
                val signatureImageInfos = dg7File.images
                if (!signatureImageInfos.isEmpty()) {
                    val displayedImageInfo = signatureImageInfos.iterator().next()
                    val image: Image = ImageUtil.getImage(context, displayedImageInfo)
                    personDetails.portraitImage = image.bitmapImage
                    personDetails.portraitImageBase64 = image.base64Image
                }
            } catch (e: Exception) {
                Log.w("", e)
            }
            publishProgress("Reading Security Options")

            // -- Security Options (if exist) -- //
            try {
                val dg14In = service.getInputStream(PassportService.EF_DG14)
                val dg14File = DG14File(dg14In)
                val dg14StoredHash = sodFile.dataGroupHashes[14]
                val dg14ComputedHash = digest.digest(dg14File.encoded)
                Log.d(
                    "",
                    "DG14 Stored Hash: " + StringUtil.byteArrayToHex(dg14StoredHash!!)
                )
                Log.d(
                    "",
                    "DG14 Computed Hash: " + StringUtil.byteArrayToHex(dg14ComputedHash)
                )
                if (Arrays.equals(dg14StoredHash, dg14ComputedHash)) {
                    Log.d(
                        "",
                        "DG14 Hashes are matched"
                    )
                } else {
                    hashesMatched = false
                }

                // Chip Authentication
                val eaccaResults: MutableList<EACCAResult> = ArrayList()
                val chipAuthenticationPublicKeyInfos: MutableList<ChipAuthenticationPublicKeyInfo> =
                    ArrayList()
                var chipAuthenticationInfo: ChipAuthenticationInfo? = null
                if (!dg14File.securityInfos.isEmpty()) {
                    for (securityInfo in dg14File.securityInfos) {
                        Log.d(
                            "",
                            "DG14 Security Info Identifier: " + securityInfo.objectIdentifier
                        )
                        if (securityInfo is ChipAuthenticationInfo) {
                            chipAuthenticationInfo = securityInfo
                        } else if (securityInfo is ChipAuthenticationPublicKeyInfo) {
                            chipAuthenticationPublicKeyInfos.add(securityInfo)
                        }
                    }
                    for (chipAuthenticationPublicKeyInfo in chipAuthenticationPublicKeyInfos) {
                        if (chipAuthenticationInfo != null) {
                            val eaccaResult = service.doEACCA(
                                chipAuthenticationInfo.keyId,
                                chipAuthenticationInfo.objectIdentifier,
                                chipAuthenticationInfo.protocolOIDString,
                                chipAuthenticationPublicKeyInfo.subjectPublicKey
                            )
                            eaccaResults.add(eaccaResult)
                        } else {
                            Log.d(
                                "",
                                "Chip Authentication failed for key: $chipAuthenticationPublicKeyInfo"
                            )
                        }
                    }
                    if (eaccaResults.size == 0) chipAuth = false
                }

                /*
                    if (paceSucceeded) {
                        service.doEACTA(caReference, terminalCerts, privateKey, null, eaccaResults.get(0), mrzInfo.getDocumentNumber())
                    } else {
                        service.doEACTA(caReference, terminalCerts, privateKey, null, eaccaResults.get(0), paceSucceeded)
                    }
                */
            } catch (e: Exception) {
                Log.w("", e)
            }
            publishProgress("Reading Document (Active Authentication) Public Key")

            // -- Document (Active Authentication) Public Key -- //
            try {
                val dg15In = service.getInputStream(PassportService.EF_DG15)
                val dg15File = DG15File(dg15In)
                val dg15StoredHash = sodFile.dataGroupHashes[15]
                val dg15ComputedHash = digest.digest(dg15File.encoded)
                Log.d(
                    "",
                    "DG15 Stored Hash: " + StringUtil.byteArrayToHex(dg15StoredHash!!)
                )
                Log.d(
                    "",
                    "DG15 Computed Hash: " + StringUtil.byteArrayToHex(dg15ComputedHash)
                )
                if (Arrays.equals(dg15StoredHash, dg15ComputedHash)) {
                    Log.d(
                        "",
                        "DG15 Hashes are matched"
                    )
                } else {
                    hashesMatched = false
                }
                val publicKey = dg15File.publicKey
                val publicKeyAlgorithm = publicKey.algorithm
                eDocument.docPublicKey = publicKey

                // Active Authentication
                if ("EC" == publicKeyAlgorithm || "ECDSA" == publicKeyAlgorithm) {
                    digestAlgorithm =
                        Util.inferDigestAlgorithmFromSignatureAlgorithm("SHA1WithRSA/ISO9796-2")
                }
                val sr = SecureRandom()
                val challenge = ByteArray(8)
                sr.nextBytes(challenge)
                val result = service.doAA(
                    dg15File.publicKey,
                    sodFile.digestAlgorithm,
                    sodFile.signerInfoDigestAlgorithm,
                    challenge
                )

                activeAuth = SecurityUtil.verifyAA(
                    dg15File.publicKey,
                    digestAlgorithm,
                    digestEncryptionAlgorithm,
                    challenge,
                    result.response
                )
                Log.d(
                    "",
                    StringUtil.byteArrayToHex(result.response)
                )
            } catch (e: Exception) {
                Log.w("", e)
            }
            eDocument.docType = docType;
            eDocument.personDetails = personDetails;
            eDocument.additionalPersonDetails = additionalPersonDetails;
            eDocument.isPassiveAuth = hashesMatched;
            eDocument.isActiveAuth = activeAuth;
            eDocument.isChipAuth = chipAuth;

            //                eDocument.setDocSignatureValid(docSignatureValid);
        } catch (e: Exception) {
            return e
        }
        return null
    }

    override fun onPostExecute(exception: java.lang.Exception?) {
        exception?.printStackTrace()

        if (exception == null) {
            resultSubject.onNext(eDocument)
        } else {
            resultSubject.onError(Throwable("Error with reading task"))
        }
    }


}


class SODFileOwn(inputStream: InputStream?) : SODFile(inputStream) {
    fun readASN1Data(): ByteArray? {
        val a = SODFile::class.java.getDeclaredField("signedData");
        a.isAccessible = true

        val v : SignedData = a.get(this) as SignedData

        return v.encapContentInfo.content.toASN1Primitive().encoded
    }
}
