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

import scala.language.implicitConversions

import io.opentracing.Span
import io.opentracing.util.GlobalTracer

trait TracedMessage {
  def message: Any = this
  val activeSpan: Span
}

object TracedMessage {
  case class Wrap[T](override val message: T, activeSpan: Span) extends TracedMessage

  def wrap(message: Any)(implicit activeSpan: MaybeSpan): Any =
    wrap(activeSpan.toOption.orElse(globalSpan()).orNull, message).merge

  def wrap[T](activeSpan: Span, message: T): Either[T, Wrap[T]] = {
    if (message == null) throw new IllegalArgumentException("message cannot be null")
    Either.cond(activeSpan ne null, Wrap[T](message, activeSpan), message)
  }

  sealed trait MaybeSpan {
    def toOption: Option[Span]
  }
  case class ActiveSpan protected (span: Span) extends MaybeSpan {
    def toOption: Option[Span] = Some(span)
  }
  object ActiveSpan {
    def apply(span: Span): ActiveSpan = {
      require(span ne null, "ActiveSpan cannot be null")
      new ActiveSpan(span)
    }
  }

  case object NoSpan extends MaybeSpan {
    def toOption: Option[Span] = None
  }
  object MaybeSpan extends MaybeSpanLowPriorityImplicits {
    implicit def apply(span: Span): MaybeSpan         = Option(span).map(ActiveSpan(_)).getOrElse(NoSpan)
    implicit def apply(span: Option[Span]): MaybeSpan = apply(span.orNull)
  }

  protected trait MaybeSpanLowPriorityImplicits {
    implicit def defaultNoSpan: MaybeSpan = NoSpan
  }

  def globalSpan(): Option[Span] = Option(GlobalTracer.get.activeSpan)
}
