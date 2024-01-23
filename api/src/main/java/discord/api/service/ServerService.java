package discord.api.service;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import discord.api.common.exception.ErrorCode;
import discord.api.common.exception.RestApiException;
import discord.api.entity.Server;
import discord.api.entity.User;
import discord.api.entity.connectionEntity.UserServer;
import discord.api.entity.dtos.AddServerDto;
import discord.api.entity.dtos.ServerDto;
import discord.api.entity.enums.UserStatus;
import discord.api.repository.server.ServerRepository;
import discord.api.repository.User.UserRepository;
import discord.api.repository.UserServer.UserServerRepository;
import discord.api.repository.server.ServerRepositoryCustom;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerService {
    private final ServerRepository serverRepository;
    private final UserRepository userRepository;
    private final UserServerRepository userServerRepository;
    private final ServerRepositoryCustom serverRepositoryCustom;
    private final AwsService awsService;

    /**
     * 서버 저장
     *
     * @param addServerDto : 서버 정보
     * @param uuid : 업로드할 파일의 UUID
     * @param emailList : 서버에 초대할 유저의 이메일 리스트
     * @author Jae Wook Jeong
     */
    @Transactional
    public void addServer(AddServerDto addServerDto, @Nullable UUID uuid, List<String> emailList) {
        Server server = Server.builder()
                .name(addServerDto.getName())
                .description(addServerDto.getDescription())
                .profileImage(uuid)
                .build();

        serverRepository.save(server);

        for (String email : emailList) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.error("ServerService.addServer()");
                        throw new RestApiException(ErrorCode.EMAIL_NOT_FOUND);
                    });

            UserServer userServer = UserServer.builder()
                    .server(server)
                    .user(user)
                    .userStatus(UserStatus.OFFLINE)
                    .build();

            userServerRepository.save(userServer);
        }
    }

    /**
     * 사용자가 속한 서버 리스트 가져오기
     *
     * @param userId : 사용자 id
     * @return List<ServerDto> : 사용자가 속한 서버 리스트
     * @throws RestApiException : 파일 byte[] 로 변환 실패 시 예외 발생
     * @author Jae Wook Jeong
     */
    @Transactional
    public List<ServerDto> getServerList(Long userId) {
        List<Server> serverList = serverRepositoryCustom.getServerListByUserId(userId);

        List<UUID> uuidList = serverList.stream()
                .map(Server::getProfileImage)
                .toList();

        List<S3Object> profileList = awsService.downloadMultipartFileList(uuidList);

        Map<UUID, byte[]> profileImageMap = profileList.stream()
                .collect(Collectors.toMap(
                        s3Object -> UUID.fromString(s3Object.getKey()),
                        s3Object -> {
                            try {
                                return IOUtils.toByteArray(s3Object.getObjectContent());
                            } catch (IOException e) {
                                throw new RestApiException(ErrorCode.FILE_PROCESSING_FAIL);
                            }
                        }
                ));

        return serverList.stream()
                .map(server -> {
                    UUID uuid = server.getProfileImage();
                    String profileImage = Base64.getEncoder().encodeToString(profileImageMap.get(uuid));

                    return ServerDto.builder()
                            .id(server.getId())
                            .name(server.getName())
                            .description(server.getDescription())
                            .profileImage(profileImage)
                            .build();
                })
                .toList();
    }
}
