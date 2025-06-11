import { ExamAnswersDTO } from "./exam-answers-dto"; // Import the ExamAnswersDTO interface

export interface ExamResponseDTO {
  examId: number; // or string, depending on your backend
  userName: string;
  answers: ExamAnswersDTO[];
}