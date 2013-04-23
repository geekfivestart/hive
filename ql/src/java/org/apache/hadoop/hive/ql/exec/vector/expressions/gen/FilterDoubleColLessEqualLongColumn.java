/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package org.apache.hadoop.hive.ql.exec.vector.expressions.gen;

import org.apache.hadoop.hive.ql.exec.vector.expressions.VectorExpression;
import org.apache.hadoop.hive.ql.exec.vector.*;
import org.apache.hadoop.hive.ql.exec.vector.VectorizedRowBatch;

public class FilterDoubleColLessEqualLongColumn extends VectorExpression {
  int colNum1;
  int colNum2;

  public FilterDoubleColLessEqualLongColumn(int colNum1, int colNum2) { 
    this.colNum1 = colNum1;
    this.colNum2 = colNum2;
  }

  @Override
  public void evaluate(VectorizedRowBatch batch) {
    DoubleColumnVector inputColVector1 = (DoubleColumnVector) batch.cols[colNum1];
    LongColumnVector inputColVector2 = (LongColumnVector) batch.cols[colNum2];
    int[] sel = batch.selected;
    boolean[] nullPos1 = inputColVector1.isNull;
    boolean[] nullPos2 = inputColVector2.isNull;
    int n = batch.size;
    double[] vector1 = inputColVector1.vector;
    long[] vector2 = inputColVector2.vector;
    
    // return immediately if batch is empty
    if (n == 0) {
      return;
    }
    
    if (inputColVector1.noNulls && inputColVector2.noNulls) {
      if (inputColVector1.isRepeating && inputColVector2.isRepeating) {
        //All must be selected otherwise size would be zero
        //Repeating property will not change.
        if (!(vector1[0] <= vector2[0])) {
          batch.size = 0;
        }
      } else if (inputColVector1.isRepeating) {
        if (batch.selectedInUse) {
          int newSize = 0;
          for(int j=0; j != n; j++) {
            int i = sel[j];
            if (vector1[0] <= vector2[i]) {
              sel[newSize++] = i;
            }
          }
          batch.size = newSize;
        } else {
          int newSize = 0;
          for(int i = 0; i != n; i++) {
            if (vector1[0] <= vector2[i]) {
              sel[newSize++] = i;
            }
          }
          if (newSize < batch.size) {
            batch.size = newSize;
            batch.selectedInUse = true;
          }
        }
      } else if (inputColVector2.isRepeating) {
        if (batch.selectedInUse) {
          int newSize = 0;
          for(int j=0; j != n; j++) {
            int i = sel[j];
            if (vector1[i] <= vector2[0]) {
              sel[newSize++] = i;
            }
          }
          batch.size = newSize;
        } else {
          int newSize = 0;
          for(int i = 0; i != n; i++) {
            if (vector1[i] <= vector2[0]) {
              sel[newSize++] = i;
            }
          }
          if (newSize < batch.size) {
            batch.size = newSize;
            batch.selectedInUse = true;
          }
        }
      } else if (batch.selectedInUse) {
        int newSize = 0;
        for(int j=0; j != n; j++) {
          int i = sel[j];
          if (vector1[i] <= vector2[i]) {
            sel[newSize++] = i;
          }
        }
        batch.size = newSize;
      } else {
        int newSize = 0;
        for(int i = 0; i != n; i++) {
          if (vector1[i] <=  vector2[i]) {
            sel[newSize++] = i;
          }
        }
        if (newSize < batch.size) {
          batch.size = newSize;
          batch.selectedInUse = true;
        }
      }
    } else if (inputColVector1.isRepeating && inputColVector2.isRepeating) {
      if (nullPos1[0] || nullPos2[0]) {
        batch.size = 0; 
      } 
    } else if (inputColVector1.isRepeating) {
      if (nullPos1[0]) {
        batch.size = 0;
      } else {
        if (batch.selectedInUse) {
          int newSize = 0;
          for(int j=0; j != n; j++) {
            int i = sel[j];
            if (!nullPos2[i]) {
              if (vector1[0] <= vector2[i]) {
                sel[newSize++] = i;
              }
            }
          }
          batch.size = newSize;
        } else {
          int newSize = 0;
          for(int i = 0; i != n; i++) {
            if (!nullPos2[i]) {
              if (vector1[0] <= vector2[i]) {
                sel[newSize++] = i;
              }
            }
          }
          if (newSize < batch.size) {
            batch.size = newSize;
            batch.selectedInUse = true;
          }
        }
      }
    } else if (inputColVector2.isRepeating) {
      if (nullPos2[0]) {
        batch.size = 0;
      } else {
        if (batch.selectedInUse) {
          int newSize = 0;
          for(int j=0; j != n; j++) {
            int i = sel[j];
            if (!nullPos1[i]) {
              if (vector1[i] <= vector2[0]) {
                sel[newSize++] = i;
              }
            }
          }
          batch.size = newSize;
        } else {
          int newSize = 0;
          for(int i = 0; i != n; i++) {
            if (!nullPos1[i]) {
              if (vector1[i] <= vector2[0]) {
                sel[newSize++] = i;
              }
            }
          }
          if (newSize < batch.size) {
            batch.size = newSize;
            batch.selectedInUse = true;
          }
        }
      }
    } else if (batch.selectedInUse) {
      int newSize = 0;
      for(int j=0; j != n; j++) {
        int i = sel[j];
        if (!nullPos1[i] && !nullPos2[i]) {
          if (vector1[i] <= vector2[i]) {
            sel[newSize++] = i;
          }
        }
      }
      batch.size = newSize;
    } else {
      int newSize = 0;
      for(int i = 0; i != n; i++) {
        if (!nullPos1[i] && !nullPos2[i]) {
          if (vector1[i] <= vector2[i]) {
            sel[newSize++] = i;
          }
        }
      }
      if (newSize < batch.size) {
        batch.size = newSize;
        batch.selectedInUse = true;
      }
    }
  }

  @Override
  public String getOutputType() {
    return "boolean";
  }

  @Override
  public int getOutputColumn() {
    return -1;
  }
}
