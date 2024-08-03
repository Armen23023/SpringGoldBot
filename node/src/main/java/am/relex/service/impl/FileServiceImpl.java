package am.relex.service.impl;
import am.relex.dao.BinaryContentDAO;
import am.relex.dao.GoldDataDAO;
import am.relex.entity.GoldData;
import am.relex.service.FileService;
import am.relex.service.enums.LinkType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;


@Service
@Log4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    @Value("${token}")
    private String token;
    @Value("${service.file_info.uri}")
    private String fileInfoUri;
    @Value("${service.file_storage.uri}")
    private String fileStorageUri;

    @Value("${link.address}")
    private String linkAddress;
    private final GoldDataDAO goldDataDAO;
    private final BinaryContentDAO binaryContentDAO;
    private final Hashids hashids;




    @Override
    public GoldData processPhoto(Message telegramMessage) {
        return null;
    }

    @Override
    public String generateLink(Long docId, LinkType linkType) {
        var hash = hashids.encode(docId);
        return linkAddress + "/api/" + linkType + "?id=" + hash;
    }
}
