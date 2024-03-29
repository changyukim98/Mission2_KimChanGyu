# 쇼핑몰 프로젝트
- [패키지 구조](md/pacakge.md)
## 필수 과제
- [사용자 인증 및 권한 처리](md/auth.md)
- [중고거래 중개하기](md/useditem.md)
- [쇼핑몰 운영하기](md/shop.md)

## 추가 과제
- [사업자 자동 로그인 방지 (NCP Capcha)](md/ncp-captcha.md)


## 어려웠던 점
- #### 파일 관리
  - 프로젝트 규모가 커질수록 새로 만드는 클래스 파일들의 위치를 어디에 둬야하나 계속 고민하게 되었다. 처음부터 어떤 클래스가 필요할지 미리 생각해두고 개발을 시작했으면 이런 시간낭비가 덜 했을 것 같다.

  - DTO를 만드는 기준에 대해서도 고민을 많이 한것 같다. 컨트롤러에서 요청과 응답에 대해서 DTO로 통신하게 되는데, 간단한 String 하나에 대한 응답도 DTO를 만들어서 보내줘야하는지, 아니면 다른 기능이지만 비슷한 응답을 주는 경우 포괄적인 하나의 DTO를 공통으로 써도 되는지, 고민을 좀 하게 되었다. 아직도 의문점이 남았지만 말이다.

- #### API 사용하기
  - API 문서만 읽고 내 프로젝트의 적용하는 것은 생각보다 헤메게 되는 일이었다.  
  API를 사용해 돌아온 응답을 DTO로 받으려고 했는데 Converting 에러가 계속 발생해 시간을 많이 잡아 먹었다. 이유는 돌아오는 응답이 text/plain 형식의 Json 문자열이었기 때문이었다. 이것을 String으로 받아 Gson을 이용해 직접 Json으로 파싱하니 문제가 해결되었다.

  - 위 이유로 시간을 소모해서 NCP 캡차 기능을 캡차 이미지를 받아오는데까지는 성공했으나 실제 로그인 기능에 추가하진 못했다. 아쉬운점이다.