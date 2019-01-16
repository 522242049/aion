package org.aion.zero.impl.blockchain;

import java.math.BigInteger;
import java.util.List;
import org.aion.base.db.IRepository;
import org.aion.base.type.Address;
import org.aion.zero.blockchain.IPowChain;
import org.aion.zero.impl.AionHub;
import org.aion.zero.impl.core.IAionBlockchain;
import org.aion.zero.impl.query.QueryInterface;
import org.aion.zero.impl.types.AionBlock;
import org.aion.zero.impl.types.AionTxInfo;
import org.aion.zero.types.A0BlockHeader;
import org.aion.zero.types.AionTransaction;
import org.aion.zero.types.AionTxReceipt;
import org.aion.zero.types.IAionBlock;

/**
 * Aion chain interface.
 * 
 */
public interface IAionChain extends QueryInterface {

    IAionBlockchain getBlockchain();

    void close();

    AionTransaction createTransaction(BigInteger nonce, Address to, BigInteger value, byte[] data);

    void broadcastTransaction(AionTransaction transaction);

    AionTxReceipt callConstant(AionTransaction tx, IAionBlock block);

    IRepository<?, ?, ?> getRepository();

    IRepository<?, ?, ?> getPendingState();

    IRepository<?, ?, ?> getSnapshotTo(byte[] root);

    List<AionTransaction> getWireTransactions();

    List<AionTransaction> getPendingStateTransactions();

    AionHub getAionHub();

    void exitOn(long number);

    long estimateTxNrg(AionTransaction tx, IAionBlock block);
}
