/*
 * Copyright 2018 The OpenTracing Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.opentracing.contrib.akka

import akka.AroundReceiveActor
import akka.actor.Actor
import io.opentracing.{ Scope, ScopeManager, Tracer }
import io.opentracing.util.GlobalTracer


trait TracedAbstractActor extends Actor with AroundReceiveActor {
  protected def finishSpanOnClose: Boolean = false

  override protected def traceBeforeReceive(receive: Receive, msg: Any): Unit = msg match {
    case traced: TracedMessage if traced.activeSpan ne null =>
      scopeManager().foreach(_.activate(traced.activeSpan, finishSpanOnClose))
      superAroundReceive(receive, traced.message)
    case _ =>
      superAroundReceive(receive, msg)
  }

  override protected def traceAfterReceive(receive: Receive, msg: Any): Unit =
    activeScope().foreach(_.close())

  protected def globalTracer(): Option[Tracer] = Option(GlobalTracer.get())
  protected def scopeManager(): Option[ScopeManager] = globalTracer().flatMap(Option apply _.scopeManager())
  protected def activeScope(): Option[Scope] = scopeManager.flatMap(Option apply _.active())
}
