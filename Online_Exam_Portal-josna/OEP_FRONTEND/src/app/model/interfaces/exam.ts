import { Question } from "./question";

export interface Exam {
    examId: number;
    title: string;
    description: string;
    totalMarks: number;
    duration: number; // in minutes

    questions?: Question[]; 
}
