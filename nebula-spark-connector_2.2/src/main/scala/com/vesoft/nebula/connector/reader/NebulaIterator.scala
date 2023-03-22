/* Copyright (c) 2022 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.connector.reader

import com.vesoft.nebula.connector.{NebulaOptions, NebulaUtils, PartitionUtils}
import org.apache.spark.Partition
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.types.StructType
import org.slf4j.{Logger, LoggerFactory}

/**
  * iterator for nebula vertex or edge data
  * convert each vertex data or edge data to Spark SQL's Row
  */
abstract class NebulaIterator extends Iterator[InternalRow] with NebulaReader {
  private val LOG: Logger = LoggerFactory.getLogger(this.getClass)

  def this(index: Partition, nebulaOptions: NebulaOptions, schema: StructType) {
    this()
    val totalPart = super.init(index.index, nebulaOptions, schema)
    // index starts with 0
    val nebulaPartition = index.asInstanceOf[NebulaPartition]
    val scanParts =
      nebulaPartition.getScanParts(totalPart, nebulaOptions.partitionNums.toInt)
    LOG.info(s"partition index: ${index}, scanParts: ${scanParts.toString}")
    scanPartIterator = scanParts.iterator
  }

  /**
    * whether this iterator can provide another element.
    */
  override def hasNext: Boolean

  /**
    * Produces the next vertex or edge of this iterator.
    */
  override def next(): InternalRow = super.getRow()
}
