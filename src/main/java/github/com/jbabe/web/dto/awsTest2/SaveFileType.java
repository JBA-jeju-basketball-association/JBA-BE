package github.com.jbabe.web.dto.awsTest2;

public enum SaveFileType {
    video("동영상"),
    image("이미지");
    private final String kor;

    SaveFileType(String kor) {
        this.kor = kor;
    }
    public String getTypeKor(){
        return this.kor;
    }
}
