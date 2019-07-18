/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.core.optimize.sharding.engnie.dml;

import org.apache.shardingsphere.core.metadata.table.ShardingTableMetaData;
import org.apache.shardingsphere.core.optimize.common.OptimizeEngine;
import org.apache.shardingsphere.core.optimize.encrypt.engine.WhereClauseEncryptConditionEngine;
import org.apache.shardingsphere.core.optimize.encrypt.statement.condition.EncryptConditions;
import org.apache.shardingsphere.core.optimize.sharding.statement.dml.ShardingWhereOptimizedStatement;
import org.apache.shardingsphere.core.optimize.sharding.statement.dml.condition.ShardingConditions;
import org.apache.shardingsphere.core.optimize.sharding.statement.dml.condition.engine.WhereClauseShardingConditionEngine;
import org.apache.shardingsphere.core.parse.sql.statement.dml.UpdateStatement;
import org.apache.shardingsphere.core.rule.ShardingRule;

import java.util.List;

/**
 * Update optimize engine for sharding.
 *
 * @author zhangliang
 */
public final class ShardingUpdateOptimizeEngine implements OptimizeEngine {
    
    private final UpdateStatement updateStatement;
    
    private final List<Object> parameters;
    
    private final WhereClauseShardingConditionEngine shardingConditionEngine;
    
    private final WhereClauseEncryptConditionEngine encryptConditionEngine;
    
    public ShardingUpdateOptimizeEngine(final ShardingRule shardingRule, final ShardingTableMetaData shardingTableMetaData, final UpdateStatement updateStatement, final List<Object> parameters) {
        this.updateStatement = updateStatement;
        this.parameters = parameters;
        shardingConditionEngine = new WhereClauseShardingConditionEngine(shardingRule, shardingTableMetaData);
        encryptConditionEngine = new WhereClauseEncryptConditionEngine(shardingRule.getEncryptRule(), shardingTableMetaData);
    }
    
    @Override
    public ShardingWhereOptimizedStatement optimize() {
        return new ShardingWhereOptimizedStatement(updateStatement,
                new ShardingConditions(shardingConditionEngine.createShardingConditions(updateStatement, parameters)),
                new EncryptConditions(encryptConditionEngine.createEncryptConditions(updateStatement)));
    }
}