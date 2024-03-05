package org.freedomtool.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.tuples.generated.Tuple3;
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
public class Registration extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_REGISTRATIONINFO = "registrationInfo";

    public static final String FUNC_ROOTSHISTORY = "rootsHistory";

    @Deprecated
    protected Registration(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Registration(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Registration(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Registration(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<Tuple3<String, RegistrationValues, RegistrationCounters>> registrationInfo() {
        final Function function = new Function(FUNC_REGISTRATIONINFO, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<RegistrationValues>() {}, new TypeReference<RegistrationCounters>() {}));
        return new RemoteFunctionCall<Tuple3<String, RegistrationValues, RegistrationCounters>>(function,
                new Callable<Tuple3<String, RegistrationValues, RegistrationCounters>>() {
                    @Override
                    public Tuple3<String, RegistrationValues, RegistrationCounters> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<String, RegistrationValues, RegistrationCounters>(
                                (String) results.get(0).getValue(), 
                                (RegistrationValues) results.get(1), 
                                (RegistrationCounters) results.get(2));
                    }
                });
    }

    public RemoteFunctionCall<Boolean> rootsHistory(byte[] param0) {
        final Function function = new Function(FUNC_ROOTSHISTORY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    @Deprecated
    public static Registration load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Registration(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Registration load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Registration(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Registration load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Registration(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Registration load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Registration(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class RegistrationValues extends StaticStruct {
        public BigInteger commitmentStartTime;

        public BigInteger commitmentEndTime;

        public RegistrationValues(BigInteger commitmentStartTime, BigInteger commitmentEndTime) {
            super(new org.web3j.abi.datatypes.generated.Uint256(commitmentStartTime), 
                    new org.web3j.abi.datatypes.generated.Uint256(commitmentEndTime));
            this.commitmentStartTime = commitmentStartTime;
            this.commitmentEndTime = commitmentEndTime;
        }

        public RegistrationValues(Uint256 commitmentStartTime, Uint256 commitmentEndTime) {
            super(commitmentStartTime, commitmentEndTime);
            this.commitmentStartTime = commitmentStartTime.getValue();
            this.commitmentEndTime = commitmentEndTime.getValue();
        }
    }

    public static class RegistrationCounters extends StaticStruct {
        public BigInteger totalRegistrations;

        public RegistrationCounters(BigInteger totalRegistrations) {
            super(new org.web3j.abi.datatypes.generated.Uint256(totalRegistrations));
            this.totalRegistrations = totalRegistrations;
        }

        public RegistrationCounters(Uint256 totalRegistrations) {
            super(totalRegistrations);
            this.totalRegistrations = totalRegistrations.getValue();
        }
    }
}
