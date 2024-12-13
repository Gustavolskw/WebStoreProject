<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\User;
use Illuminate\Http\Request;
use PHPOpenSourceSaver\JWTAuth\Facades\JWTAuth;  // Usando o JWTAuth correto

class UserController extends Controller
{

    public function __construct(User $user)
    {
        //  $this->user = $user;
    }

    public function me()
    {
        // use auth()->user() to get authenticated user data

        return response()->json([
            'meta' => [
                'code' => 200,
                'status' => 'success',
                'message' => 'User fetched successfully!',
            ],
            'data' => [
                'user' => JWTAuth::user(),
            ],
        ]);
    }
}
