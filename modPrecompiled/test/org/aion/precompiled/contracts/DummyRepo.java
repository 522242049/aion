package org.aion.precompiled.contracts;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.aion.base.db.IContractDetails;
import org.aion.base.db.IRepository;
import org.aion.base.db.IRepositoryCache;
import org.aion.base.type.AionAddress;
import org.aion.base.util.ByteUtil;
import org.aion.base.vm.IDataWord;
import org.aion.mcf.core.AccountState;
import org.aion.mcf.db.IBlockStoreBase;
import org.aion.mcf.vm.types.DataWord;
import org.aion.mcf.vm.types.DoubleDataWord;

public class DummyRepo implements IRepositoryCache<AccountState, IDataWord, IBlockStoreBase<?, ?>> {
    private Map<AionAddress, AccountState> accounts = new HashMap<>();
    private Map<AionAddress, byte[]> contracts = new HashMap<>();
    private Map<AionAddress, Map<String, byte[]>> storage = new HashMap<>();

    // Made this alterable for testing since this default value is not always what real
    // implementations
    // do ... and don't want to break tests that rely on this value.
    public IDataWord storageErrorReturn = DoubleDataWord.ZERO;

    public DummyRepo() {}

    public DummyRepo(DummyRepo parent) {
        // Note: only references are copied
        accounts.putAll(parent.accounts);
        contracts.putAll(parent.contracts);
        storage.putAll(parent.storage);
    }

    void addContract(AionAddress address, byte[] code) {
        contracts.put(address, code);
    }

    @Override
    public AccountState createAccount(AionAddress addr) {
        AccountState as = new AccountState();
        accounts.put(addr, as);
        return as;
    }

    @Override
    public boolean hasAccountState(AionAddress addr) {
        return accounts.containsKey(addr);
    }

    @Override
    public AccountState getAccountState(AionAddress addr) {
        if (!hasAccountState(addr)) {
            createAccount(addr);
        }
        return accounts.get(addr);
    }

    @Override
    public void deleteAccount(AionAddress addr) {
        accounts.remove(addr);
    }

    // Throws exception if account does not exist.
    @Override
    public BigInteger incrementNonce(AionAddress addr) {
        AccountState as = getAccountState(addr);
        as.incrementNonce();
        return as.getNonce();
    }

    @Override
    public BigInteger setNonce(AionAddress address, BigInteger nonce) {
        throw new RuntimeException("Not supported");
    }

    // an exception will be thrown if account does not exist
    @Override
    public BigInteger getNonce(AionAddress addr) {
        return getAccountState(addr).getNonce();
    }

    @Override
    public IContractDetails<IDataWord> getContractDetails(AionAddress addr) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasContractDetails(AionAddress addr) {
        return contracts.containsKey(addr);
    }

    @Override
    public void saveCode(AionAddress addr, byte[] code) {
        contracts.put(addr, code);
    }

    @Override
    public byte[] getCode(AionAddress addr) {
        byte[] code = contracts.get(addr);
        return code == null ? ByteUtil.EMPTY_BYTE_ARRAY : code;
    }

    @Override
    public Map<IDataWord, IDataWord> getStorage(AionAddress address, Collection<IDataWord> keys) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public void addStorageRow(AionAddress addr, IDataWord key, IDataWord value) {
        Map<String, byte[]> map = storage.computeIfAbsent(addr, k -> new HashMap<>());
        map.put(key.toString(), value.getData());
    }

    @Override
    public IDataWord getStorageValue(AionAddress addr, IDataWord key) {
        Map<String, byte[]> map = storage.get(addr);
        if (map != null && map.containsKey(key.toString())) {
            byte[] res = map.get(key.toString());
            if (res.length == DataWord.BYTES) {
                return new DataWord(res);
            } else if (res.length == DoubleDataWord.BYTES) {
                return new DoubleDataWord(res);
            }
        }
        return storageErrorReturn;
    }

    @Override
    public List<byte[]> getPoolTx() {
        return null;
    }

    @Override
    public List<byte[]> getCacheTx() {
        return null;
    }

    @Override
    public BigInteger getBalance(AionAddress addr) {
        return getAccountState(addr).getBalance();
    }

    @Override
    public BigInteger addBalance(AionAddress addr, BigInteger value) {
        return getAccountState(addr).addToBalance(value);
    }

    @Override
    public IRepositoryCache<AccountState, IDataWord, IBlockStoreBase<?, ?>> startTracking() {
        return new DummyRepo(this);
    }

    @Override
    public void flush() {}

    @Override
    public void rollback() {}

    @Override
    public void syncToRoot(byte[] root) {}

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void close() {}

    @Override
    public boolean isValidRoot(byte[] root) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBatch(
            Map<AionAddress, AccountState> accountStates,
            Map<AionAddress, IContractDetails<IDataWord>> contractDetailes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] getRoot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void loadAccountState(
            AionAddress addr,
            Map<AionAddress, AccountState> cacheAccounts,
            Map<AionAddress, IContractDetails<IDataWord>> cacheDetails) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IRepository<AccountState, IDataWord, IBlockStoreBase<?, ?>> getSnapshotTo(byte[] root) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IBlockStoreBase<?, ?> getBlockStore() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addTxBatch(Map<byte[], byte[]> pendingTx, boolean isPool) {}

    @Override
    public void removeTxBatch(Set<byte[]> pendingTx, boolean isPool) {}

    @Override
    public void compact() {
        throw new UnsupportedOperationException(
                "The tracking cache cannot be compacted. \'Compact\' should be called on the tracked repository.");
    }

    @Override
    public boolean isIndexed(byte[] hash, long level) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSnapshot() {
        throw new UnsupportedOperationException();
    }
}
