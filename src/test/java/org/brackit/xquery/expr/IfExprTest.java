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

import org.brackit.xquery.BrackitQueryContext;
import org.brackit.xquery.QueryContext;
import org.brackit.xquery.ResultChecker;
import org.brackit.xquery.atomic.Bool;
import org.brackit.xquery.atomic.Int32;
import org.brackit.xquery.operator.TupleImpl;
import org.junit.Test;

/**
 * @author Sebastian Baechle
 */
public class IfExprTest {
  QueryContext ctx = new BrackitQueryContext(null, null);

  @Test
  public void emptyCondition() throws Exception {
    IfExpr expr = new IfExpr(new EmptyExpr(), Bool.TRUE, Bool.FALSE);
    ResultChecker.dCheck(Bool.FALSE, expr.evaluate(ctx, new TupleImpl()));
  }

  @Test
  public void literalTrueCondition() throws Exception {
    IfExpr expr = new IfExpr(new Int32(1), Bool.TRUE, Bool.FALSE);
    ResultChecker.dCheck(Bool.TRUE, expr.evaluate(ctx, new TupleImpl()));
  }

  @Test
  public void literalFalseCondition() throws Exception {
    IfExpr expr = new IfExpr(new Int32(0), Bool.TRUE, Bool.FALSE);
    ResultChecker.dCheck(Bool.FALSE, expr.evaluate(ctx, new TupleImpl()));
  }
}
