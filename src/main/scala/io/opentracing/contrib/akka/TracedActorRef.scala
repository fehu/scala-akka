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
package io.opentracing.contrib.akka

import scala.concurrent.Future

import akka.actor.{ Actor, ActorContext, ActorRef }
import akka.pattern.ask
import akka.util.Timeout
import io.opentracing.contrib.akka.TracedMessage.MaybeSpan

class TracedActorRef(val ref: ActorRef) extends AnyVal {
  import TracedMessage.wrap

  def !(msg: TracedMessage)(implicit sender: ActorRef = Actor.noSender): Unit = ref ! msg
  def !(msg: Any)(implicit span: MaybeSpan, sender: ActorRef = Actor.noSender): Unit = ref ! wrap(msg)

  def tell(msg: TracedMessage, sender: ActorRef): Unit = this.!(msg)(sender)
  def tell(msg: Any, sender: ActorRef)(implicit span: MaybeSpan): Unit = this.!(msg)(span, sender)
  def tell(msg: Any, sender: ActorRef, span: MaybeSpan): Unit = this.!(msg)(span, sender)

  def forward(traced: TracedMessage)(implicit context: ActorContext): Unit = tell(traced, context.sender())
  def forward(msg: Any)(implicit span: MaybeSpan, context: ActorContext): Unit = tell(msg, context.sender())

  def ?(msg: TracedMessage)(implicit timeout: Timeout, sender: ActorRef = Actor.noSender): Future[Any] = ref ? msg
  def ?(msg: Any)(implicit span: MaybeSpan, timeout: Timeout, sender: ActorRef = Actor.noSender): Future[Any] = ref ? wrap(msg)

  def ask(msg: TracedMessage)(implicit timeout: Timeout, sender: ActorRef = Actor.noSender): Future[Any] = this.?(msg)
  def ask(msg: Any)(implicit span: MaybeSpan, timeout: Timeout, sender: ActorRef = Actor.noSender): Future[Any] = this.?(msg)

}
