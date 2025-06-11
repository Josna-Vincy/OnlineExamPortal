export interface AuthResponse {
    success: boolean;
    statusCode: number;
    data: string | null; // The JWT token
    message: string | null;
}
