"""Queue engine: `b4n1-db queue <verb>`."""

from .._base import EngineGroup


class Queue(EngineGroup):
    """FIFO queues with ack/nack."""

    def push(self, queue, body):
        """Append a message and return its id."""
        return self._call("queue", "push", queue, body)

    def pop(self, queue):
        """Take the oldest pending message out of the queue (in flight)."""
        return self._call("queue", "pop", queue)

    def peek(self, queue):
        """Look at the oldest pending message without taking it."""
        return self._call("queue", "peek", queue)

    def ack(self, queue, message_id):
        """Confirm a message so its parked payload is dropped."""
        return self._call("queue", "ack", queue, message_id)

    def nack(self, queue, message_id):
        """Requeue an in-flight message at its original position."""
        return self._call("queue", "nack", queue, message_id)

    def clear(self, queue):
        """Empty the queue."""
        return self._call("queue", "clear", queue)

    def len(self, queue):
        """Count the pending messages."""
        return self._call("queue", "len", queue)
