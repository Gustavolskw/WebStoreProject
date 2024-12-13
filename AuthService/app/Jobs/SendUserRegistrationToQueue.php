<?php

namespace App\Jobs;

use App\Models\User;
use Illuminate\Bus\Queueable;
use Illuminate\Contracts\Queue\ShouldQueue;
use Illuminate\Queue\InteractsWithQueue;
use Illuminate\Queue\SerializesModels;
use Illuminate\Support\Facades\Log;
use PhpAmqpLib\Message\AMQPMessage;
use PhpAmqpLib\Connection\AMQPStreamConnection;

class SendUserRegistrationToQueue implements ShouldQueue
{
    use Queueable, InteractsWithQueue, SerializesModels;

    protected $user;

    /**
     * Create a new job instance.
     *
     * @param User $user
     */
    public function __construct(User $user)
    {
        $this->user = $user;
    }

    /**
     * Execute the job.
     *
     * @return void
     */
    public function handle()
    {
        // Set up RabbitMQ connection
        $connection = new AMQPStreamConnection(
            env('RABBITMQ_HOST'),
            env('RABBITMQ_PORT'),
            env('RABBITMQ_USER'),
            env('RABBITMQ_PASSWORD'),
            env('RABBITMQ_VHOST')
        );

        // Create a channel
        $channel = $connection->channel();

        // Define the queue
        $queue = env('RABBITMQ_QUEUE_USER_REGISTER');

        // Declare the queue (ensuring it exists)
        $channel->queue_declare($queue, false, true, false, false);

        // Prepare the message
        $msg = new AMQPMessage(json_encode($this->user->toArray()));

        // Publish the message to the queue
        $channel->basic_publish($msg, '', $queue);

        // Log the event (optional)
        Log::info('User registration message sent to RabbitMQ.');

        // Close the connection and channel
        $channel->close();
        $connection->close();
    }
}
