<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Log;
use Illuminate\Support\Facades\Validator;
use PhpAmqpLib\Connection\AMQPStreamConnection;
use PhpAmqpLib\Message\AMQPMessage;
use PHPOpenSourceSaver\JWTAuth\Facades\JWTAuth;

class AuthController extends Controller
{
    public function __construct()
    {
        // No dependency injection needed for JWTAuth package
    }

    // User Registration Method
    public function register(Request $request)
    {
        // Validate request data
        $validator = Validator::make($request->all(), [
            'name' => 'required|string|min:2|max:255',
            'email' => 'required|string|email:rfc,dns|max:255|unique:users',
            'password' => 'required|string|min:6|max:255',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'meta' => [
                    'code' => 422,
                    'status' => 'error',
                    'message' => 'Validation failed.',
                ],
                'data' => $validator->errors(),
            ], 422);
        }

        // Create new user
        $user = User::create([
            'name' => $request['name'],
            'email' => $request['email'],
            'password' => bcrypt($request['password']),
        ]);

        // Generate JWT token for the user
        $token = JWTAuth::attempt($request->only('email', 'password'));

        // Send user registration data to RabbitMQ directly
        $this->sendToRabbitMQ($user);

        // Return success response with token
        return response()->json([
            'meta' => [
                'code' => 200,
                'status' => 'success',
                'message' => 'User created successfully!',
            ],
            'data' => [
                'user' => $user,
                'access_token' => [
                    'token' => $token,
                    'type' => 'Bearer',
                    'expires_in' => 60 * 60, // Token expires in 1 hour
                ],
            ],
        ]);
    }

    // Send user data to RabbitMQ directly
    protected function sendToRabbitMQ($user)
    {
        // Create a connection to RabbitMQ
        $connection = new AMQPStreamConnection(
            env('RABBITMQ_HOST'),
            env('RABBITMQ_PORT'),
            env('RABBITMQ_USER'),
            env('RABBITMQ_PASSWORD'),
            env('RABBITMQ_VHOST')
        );

        $channel = $connection->channel();

        // Define the exchange and queue name (you can use the same names you declared before)
        $exchange = env('RABBITMQ_EXCHANGE', 'user_exchange');
        $queue = env('RABBITMQ_QUEUE_USER_REGISTER', 'USER_REGISTER_QUEUE');

        // Declare the fanout exchange (if it doesn't exist)
        $channel->exchange_declare($exchange, 'fanout', false, true, false);

        // Declare the queue (if it doesn't exist)
        $channel->queue_declare($queue, false, true, false, false);

        // Bind the queue to the exchange
        $channel->queue_bind($queue, $exchange);

        // Prepare the message
        $msg = new AMQPMessage(json_encode($user->toArray()));

        // Publish the message to the queue
        $channel->basic_publish($msg, $exchange);

        // Log the event (optional)
        Log::info('User registration message sent to RabbitMQ.');

        // Close the connection and channel
        $channel->close();
        $connection->close();
    }

    // User login method
    public function login(Request $request)
    {
        // Validate login data
        $validator = Validator::make($request->all(), [
            'email' => 'required|string',
            'password' => 'required|string',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'meta' => [
                    'code' => 422,
                    'status' => 'error',
                    'message' => 'Validation failed.',
                ],
                'data' => $validator->errors(),
            ], 422);
        }

        // Attempt to authenticate the user and generate the token
        $token = JWTAuth::attempt($request->only('email', 'password'));

        if ($token) {
            return response()->json([
                'meta' => [
                    'code' => 200,
                    'status' => 'success',
                    'message' => 'User logged in successfully.',
                ],
                'data' => [
                    'user' => JWTAuth::user(), // Get the authenticated user
                    'access_token' => [
                        'token' => $token,
                        'type' => 'Bearer',
                        'expires_in' => 60 * 60, // Token expires in 1 hour
                    ],
                ],
            ]);
        }

        // Return error if login fails
        return response()->json([
            'meta' => [
                'code' => 401,
                'status' => 'error',
                'message' => 'Invalid credentials.',
            ],
            'data' => [],
        ], 401);
    }

    // User logout method
    public function logout()
    {
        // Get the current token
        $token = JWTAuth::getToken();

        // Invalidate the token
        JWTAuth::invalidate($token);

        return response()->json([
            'meta' => [
                'code' => 200,
                'status' => 'success',
                'message' => 'Successfully logged out.',
            ],
            'data' => [],
        ]);
    }
}
