package com.pivotal.gemfirexd.internal.engine.distributed.message;

import com.gemstone.gemfire.DataSerializer;
import com.gemstone.gemfire.distributed.DistributedMember;
import com.gemstone.gemfire.distributed.internal.InternalDistributedSystem;
import com.gemstone.gemfire.distributed.internal.membership.InternalDistributedMember;
import com.gemstone.gemfire.internal.shared.Version;
import com.pivotal.gemfirexd.internal.engine.GfxdConstants;
import com.pivotal.gemfirexd.internal.engine.distributed.GfxdDistributionAdvisor;
import com.pivotal.gemfirexd.internal.engine.distributed.GfxdResultCollector;
import com.pivotal.gemfirexd.internal.engine.distributed.SnappyResultHolder;
import com.pivotal.gemfirexd.internal.engine.distributed.utils.GemFireXDUtils;
import com.pivotal.gemfirexd.internal.iapi.error.StandardException;
import com.pivotal.gemfirexd.internal.shared.common.reference.SQLState;
import com.pivotal.gemfirexd.internal.shared.common.sanity.SanityManager;
import com.pivotal.gemfirexd.internal.snappy.CallbackFactoryProvider;
import com.pivotal.gemfirexd.internal.snappy.LeadNodeExecutionContext;
import com.pivotal.gemfirexd.internal.snappy.SparkSQLExecute;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Route query to Snappy Spark Lead node.
 * <p/>
 * Created by kneeraj on 20/10/15.
 */
public final class LeadNodeExecutorMsg extends MemberExecutorMessage<Object> {

  private String sql;
  private LeadNodeExecutionContext ctx;
  private transient SparkSQLExecute exec;

  public LeadNodeExecutorMsg(String sql, LeadNodeExecutionContext ctx,
      GfxdResultCollector<Object> rc) {
    super(rc, null, false);
    this.sql = sql;
    this.ctx = ctx;
  }

  /**
   * Default constructor for deserialization. Not to be invoked directly.
   */
  public LeadNodeExecutorMsg() {
    super(true);
  }

  @Override
  public Set<DistributedMember> getMembers() {
    GfxdDistributionAdvisor advisor = GemFireXDUtils.getGfxdAdvisor();
    Set<DistributedMember> allMembers = InternalDistributedSystem
        .getConnectedInstance().getAllOtherMembers();
    for (DistributedMember m : allMembers) {
      GfxdDistributionAdvisor.GfxdProfile profile = advisor
          .getProfile((InternalDistributedMember)m);
      if (profile != null && profile.hasSparkURL()) {
        Set<DistributedMember> s = new HashSet<DistributedMember>();
        s.add(m);
        return Collections.unmodifiableSet(s);
      }
    }
    return Collections.emptySet();
  }

  @Override
  public void postExecutionCallback() {

  }

  @Override
  public boolean isHA() {
    return false;
  }

  @Override
  public boolean optimizeForWrite() {
    return false;
  }

  @Override
  protected void execute() throws Exception {
    if (GemFireXDUtils.TraceQuery) {
      SanityManager.DEBUG_PRINT(GfxdConstants.TRACE_QUERYDISTRIB,
              "LeadNodeExecutorMsg.execute: Got sql = " + sql);
    }
    try {
      InternalDistributedMember m = this.getSenderForReply();
      final Version v = m.getVersionObject();
      exec = CallbackFactoryProvider.getClusterCallbacks().getSQLExecute(
          sql, ctx, v);
      SnappyResultHolder srh = new SnappyResultHolder(exec);

      srh.prepareSend(this);
      //sendresult and lastResult is called via prepareSend
//      this.sendResult(srh);
//      this.lastResult(srh);
      this.lastResultSent = true;
      this.endMessage();
      if (GemFireXDUtils.TraceQuery) {
        SanityManager.DEBUG_PRINT(GfxdConstants.TRACE_QUERYDISTRIB,
                "LeadNodeExecutorMsg.execute: Sent Last result ");
      }
    } catch (Exception ex) {
      // Catch all exceptions and convert so can be caugh at XD side
      if (ex.getClass().getName().contains("AnalysisException")) {
        throw StandardException.newException(
            SQLState.LANG_UNEXPECTED_USER_EXCEPTION, ex, ex.getMessage());
      } else if (ex.getClass().getName().contains("apache.spark.storage")) {
        throw StandardException.newException(
            SQLState.DATA_UNEXPECTED_EXCEPTION, ex, ex.getMessage());
      } else if (ex.getClass().getName().contains("apache.spark.sql")) {
        throw StandardException.newException(
            SQLState.LANG_UNEXPECTED_USER_EXCEPTION, ex, ex.getMessage());
      }
      throw ex;
    }
  }

  @Override
  protected LeadNodeExecutorMsg clone() {
    final LeadNodeExecutorMsg msg = new LeadNodeExecutorMsg(this.sql, this.ctx,
        (GfxdResultCollector<Object>)this.userCollector);
    msg.exec = this.exec;
    return msg;
  }

  @Override
  public byte getGfxdID() {
    return LEAD_NODE_EXN_MSG;
  }

  @Override
  public void fromData(DataInput in) throws IOException, ClassNotFoundException {
    super.fromData(in);
    this.sql = DataSerializer.readString(in);
    this.ctx = DataSerializer.readObject(in);
  }

  @Override
  public void toData(final DataOutput out) throws IOException {
    super.toData(out);
    DataSerializer.writeString(this.sql, out);
    DataSerializer.writeObject(ctx, out);
  }
}
