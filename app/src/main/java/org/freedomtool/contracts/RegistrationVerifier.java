package org.freedomtool.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.5.2.
 */
@SuppressWarnings("rawtypes")
public class RegistrationVerifier extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_REGISTER_PROOF_QUERY_ID = "REGISTER_PROOF_QUERY_ID";

    public static final String FUNC___REGISTERVERIFIER_INIT = "__RegisterVerifier_init";

    public static final String FUNC_COUNTISSUINGAUTHORITYBLACKLIST = "countIssuingAuthorityBlacklist";

    public static final String FUNC_COUNTISSUINGAUTHORITYWHITELIST = "countIssuingAuthorityWhitelist";

    public static final String FUNC_GETALLOWEDISSUERS = "getAllowedIssuers";

    public static final String FUNC_GETREGISTERPROOFINFO = "getRegisterProofInfo";

    public static final String FUNC_ISALLOWEDISSUER = "isAllowedIssuer";

    public static final String FUNC_ISIDENTITYREGISTERED = "isIdentityRegistered";

    public static final String FUNC_ISISSUINGAUTHORITYBLACKLISTED = "isIssuingAuthorityBlacklisted";

    public static final String FUNC_ISISSUINGAUTHORITYWHITELISTED = "isIssuingAuthorityWhitelisted";

    public static final String FUNC_LISTISSUINGAUTHORITYBLACKLIST = "listIssuingAuthorityBlacklist";

    public static final String FUNC_LISTISSUINGAUTHORITYWHITELIST = "listIssuingAuthorityWhitelist";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_PROXIABLEUUID = "proxiableUUID";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_SETZKPQUERIESSTORAGE = "setZKPQueriesStorage";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final String FUNC_UPDATEALLOWEDISSUERS = "updateAllowedIssuers";

    public static final String FUNC_ZKPQUERIESSTORAGE = "zkpQueriesStorage";

    @Deprecated
    protected RegistrationVerifier(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected RegistrationVerifier(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected RegistrationVerifier(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected RegistrationVerifier(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<String> REGISTER_PROOF_QUERY_ID() {
        final Function function = new Function(FUNC_REGISTER_PROOF_QUERY_ID, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> __RegisterVerifier_init(String zkpQueriesStorage_, List<BigInteger> issuingAuthorityWhitelist_, List<BigInteger> issuingAuthorityBlacklist_) {
        final Function function = new Function(
                FUNC___REGISTERVERIFIER_INIT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, zkpQueriesStorage_), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.datatypes.generated.Uint256.class,
                        org.web3j.abi.Utils.typeMap(issuingAuthorityWhitelist_, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.datatypes.generated.Uint256.class,
                        org.web3j.abi.Utils.typeMap(issuingAuthorityBlacklist_, org.web3j.abi.datatypes.generated.Uint256.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> countIssuingAuthorityBlacklist() {
        final Function function = new Function(FUNC_COUNTISSUINGAUTHORITYBLACKLIST, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> countIssuingAuthorityWhitelist() {
        final Function function = new Function(FUNC_COUNTISSUINGAUTHORITYWHITELIST, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<List> getAllowedIssuers(BigInteger schema_) {
        final Function function = new Function(FUNC_GETALLOWEDISSUERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(schema_)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Uint256>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<RegisterProofInfo> getRegisterProofInfo(String registrationContract_, BigInteger documentNullifier_) {
        final Function function = new Function(FUNC_GETREGISTERPROOFINFO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, registrationContract_), 
                new org.web3j.abi.datatypes.generated.Uint256(documentNullifier_)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<RegisterProofInfo>() {}));
        return executeRemoteCallSingleValueReturn(function, RegisterProofInfo.class);
    }

    public RemoteFunctionCall<Boolean> isAllowedIssuer(BigInteger schema_, BigInteger issuerId_) {
        final Function function = new Function(FUNC_ISALLOWEDISSUER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(schema_), 
                new org.web3j.abi.datatypes.generated.Uint256(issuerId_)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<Boolean> isIdentityRegistered(String registrationContract_, BigInteger documentNullifier_) {
        final Function function = new Function(FUNC_ISIDENTITYREGISTERED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, registrationContract_), 
                new org.web3j.abi.datatypes.generated.Uint256(documentNullifier_)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<Boolean> isIssuingAuthorityBlacklisted(BigInteger issuingAuthority_) {
        final Function function = new Function(FUNC_ISISSUINGAUTHORITYBLACKLISTED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(issuingAuthority_)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<Boolean> isIssuingAuthorityWhitelisted(BigInteger issuingAuthority_) {
        final Function function = new Function(FUNC_ISISSUINGAUTHORITYWHITELISTED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(issuingAuthority_)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<List> listIssuingAuthorityBlacklist(BigInteger offset_, BigInteger limit_) {
        final Function function = new Function(FUNC_LISTISSUINGAUTHORITYBLACKLIST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(offset_), 
                new org.web3j.abi.datatypes.generated.Uint256(limit_)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Uint256>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<List> listIssuingAuthorityWhitelist(BigInteger offset_, BigInteger limit_) {
        final Function function = new Function(FUNC_LISTISSUINGAUTHORITYWHITELIST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(offset_), 
                new org.web3j.abi.datatypes.generated.Uint256(limit_)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Uint256>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<String> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<byte[]> proxiableUUID() {
        final Function function = new Function(FUNC_PROXIABLEUUID, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<TransactionReceipt> renounceOwnership() {
        final Function function = new Function(
                FUNC_RENOUNCEOWNERSHIP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setZKPQueriesStorage(String newZKPQueriesStorage_) {
        final Function function = new Function(
                FUNC_SETZKPQUERIESSTORAGE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, newZKPQueriesStorage_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> transferOwnership(String newOwner) {
        final Function function = new Function(
                FUNC_TRANSFEROWNERSHIP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, newOwner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> updateAllowedIssuers(BigInteger schema_, List<BigInteger> issuerIds_, Boolean isAdding_) {
        final Function function = new Function(
                FUNC_UPDATEALLOWEDISSUERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(schema_), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.datatypes.generated.Uint256.class,
                        org.web3j.abi.Utils.typeMap(issuerIds_, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.Bool(isAdding_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> zkpQueriesStorage() {
        final Function function = new Function(FUNC_ZKPQUERIESSTORAGE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    @Deprecated
    public static RegistrationVerifier load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new RegistrationVerifier(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static RegistrationVerifier load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new RegistrationVerifier(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static RegistrationVerifier load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new RegistrationVerifier(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static RegistrationVerifier load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new RegistrationVerifier(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class RegisterProofParams extends StaticStruct {
        public BigInteger issuingAuthority;

        public BigInteger documentNullifier;

        public byte[] commitment;

        public RegisterProofParams(BigInteger issuingAuthority, BigInteger documentNullifier, byte[] commitment) {
            super(new org.web3j.abi.datatypes.generated.Uint256(issuingAuthority), 
                    new org.web3j.abi.datatypes.generated.Uint256(documentNullifier), 
                    new org.web3j.abi.datatypes.generated.Bytes32(commitment));
            this.issuingAuthority = issuingAuthority;
            this.documentNullifier = documentNullifier;
            this.commitment = commitment;
        }

        public RegisterProofParams(Uint256 issuingAuthority, Uint256 documentNullifier, Bytes32 commitment) {
            super(issuingAuthority, documentNullifier, commitment);
            this.issuingAuthority = issuingAuthority.getValue();
            this.documentNullifier = documentNullifier.getValue();
            this.commitment = commitment.getValue();
        }
    }

    public static class RegisterProofInfo extends StaticStruct {
        public RegisterProofParams registerProofParams;

        public String registrationContractAddress;

        public RegisterProofInfo(RegisterProofParams registerProofParams, String registrationContractAddress) {
            super(registerProofParams, 
                    new org.web3j.abi.datatypes.Address(160, registrationContractAddress));
            this.registerProofParams = registerProofParams;
            this.registrationContractAddress = registrationContractAddress;
        }

        public RegisterProofInfo(RegisterProofParams registerProofParams, Address registrationContractAddress) {
            super(registerProofParams, registrationContractAddress);
            this.registerProofParams = registerProofParams;
            this.registrationContractAddress = registrationContractAddress.getValue();
        }
    }
}
