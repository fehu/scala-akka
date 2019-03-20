/*
 * Copyright 2019 Dmitry K
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
package io.opentracing.contrib

import scala.concurrent.Future

import _root_.akka.actor.{ Actor, ActorContext, ActorRef }
import _root_.akka.pattern.ask
import _root_.akka.util.Timeout
import io.opentracing.Span


package object akka {
  implicit class TracingActorRefOps(ref: ActorRef) {
    def *!(msg: TracedMessage)(implicit sender: ActorRef = Actor.noSender): Unit = ref ! msg
    def *!(msg: Any)(implicit span: Span, sender: ActorRef = Actor.noSender): Unit = ref ! TracedMessage.wrap(msg)

    def *?(msg: TracedMessage)(implicit timeout: Timeout, sender: ActorRef = Actor.noSender): Future[Any] = ref ? msg
    def *?(msg: Any)(implicit span: Span, timeout: Timeout, sender: ActorRef = Actor.noSender): Future[Any] = ref ? TracedMessage.wrap(msg)

    def forwardTr(traced: TracedMessage)(implicit context: ActorContext): Unit = *!(traced)(context.sender())
    def forwardTr(msg: Any)(implicit span: Span, context: ActorContext): Unit = *!(msg)(span, context.sender())
  }
}
