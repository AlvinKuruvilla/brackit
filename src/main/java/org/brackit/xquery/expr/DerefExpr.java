/*
 * [New BSD License]
 * Copyright (c) 2011-2012, Brackit Project Team <info@brackit.org>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Brackit Project Team nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.brackit.xquery.expr;

import org.brackit.xquery.QueryContext;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.Tuple;
import org.brackit.xquery.atomic.Atomic;
import org.brackit.xquery.atomic.IntNumeric;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.compiler.Bits;
import org.brackit.xquery.sequence.BaseIter;
import org.brackit.xquery.sequence.ItemSequence;
import org.brackit.xquery.sequence.LazySequence;
import org.brackit.xquery.util.ExprUtil;
import org.brackit.xquery.xdm.Expr;
import org.brackit.xquery.xdm.Item;
import org.brackit.xquery.xdm.Iter;
import org.brackit.xquery.xdm.Sequence;
import org.brackit.xquery.xdm.json.Object;

/**
 * @author Sebastian Baechle
 */
public class DerefExpr implements Expr {

  final Expr object;
  final Expr field;

  public DerefExpr(Expr object, Expr field) {
    this.object = object;
    this.field = field;
  }

  @Override
  public Sequence evaluate(QueryContext ctx, Tuple tuple) {
    Sequence sequence = object.evaluate(ctx, tuple);

    if (sequence instanceof ItemSequence itemSequence) {
      return getLazySequence(ctx, tuple, itemSequence.iterate());
    }

    if (sequence instanceof LazySequence lazySequence) {
      return getLazySequence(ctx, tuple, lazySequence.iterate());
    }

    if (sequence instanceof BoundVariable boundVariable) {
      System.out.println();
    }

    if (!(sequence instanceof Object object)) {
      return null;
    }

    Item itemField = field.evaluateToItem(ctx, tuple);
    if (itemField == null) {
      return null;
    }
    return getSequenceByRecordField(object, itemField);
  }

  private LazySequence getLazySequence(final QueryContext ctx, final Tuple tuple, final Iter iter) {
    return new LazySequence() {
      @Override
      public Iter iterate() {
        return new BaseIter() {
          @Override
          public Item next() {
            Item item;
            while ((item = iter.next()) != null) {
              if (!(item instanceof Object object)) {
                continue;
              }

              Item itemField = field.evaluateToItem(ctx, tuple);
              if (itemField == null) {
                continue;
              }

              final var sequenceByRecordField = getSequenceByRecordField(object, itemField);
              if (sequenceByRecordField != null) {
                return sequenceByRecordField.evaluateToItem(ctx, tuple);
              }
            }
            return null;
          }

          @Override
          public void close() {
          }

          @Override
          public Split split(int min, int max) throws QueryException {
            // TODO Auto-generated method stub
            return null;
          }
        };
      }
    };
  }

  private Sequence getSequenceByRecordField(Object object, Item itemField) {
    if (itemField instanceof QNm qNmField) {
      return object.get(qNmField);
    } else if (itemField instanceof IntNumeric intNumericField) {
      return object.value(intNumericField);
    } else if (itemField instanceof Atomic atomicField) {
      return object.get(new QNm(atomicField.stringValue()));
    } else {
      throw new QueryException(Bits.BIT_ILLEGAL_OBJECT_FIELD, "Illegal object itemField reference: %s", itemField);
    }
  }

  @Override
  public Item evaluateToItem(QueryContext ctx, Tuple tuple) throws QueryException {
    return ExprUtil.asItem(evaluate(ctx, tuple));
  }

  @Override
  public boolean isUpdating() {
    if (object.isUpdating()) {
      return true;
    }
    if (field.isUpdating()) {
      return true;
    }
    return false;
  }

  @Override
  public boolean isVacuous() {
    return false;
  }

  public String toString() {
    return "=>" + field;
  }
}
