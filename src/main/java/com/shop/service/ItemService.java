package com.shop.service;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemImgDto;
import com.shop.dto.ItemSearchDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemImgRepository itemImgRepository;
    private final ItemImgService itemImgService;

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{
        Item item = itemFormDto.createItem(); // 상품 등록 폼으로부터 입력 받은 데이터를 이용해 item객체를 생성
        itemRepository.save(item); // 상품 데이터 저장

        for (int i=0 ; i<itemImgFileList.size() ; i++){
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);
            if(i==0) // 첫 번째 이미지일 경우 대표 상품 이미지 여부 값을 "Y"로 세팅, 나머지 상품 이미지는 "N"으로 세팅
                itemImg.setRepimgYn("Y");
            else
                itemImg.setRepimgYn("N");
            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i)); // 상품 이미지 정보를 저장
            }

        return item.getId();
        }

        // 등록된 상품을 불러오는 메서드
        @Transactional(readOnly = true) // 상품 데이터를 읽어오는 트랙잭션을 읽기전용으로 설정, 이럴 경우 JPA가 더티체킹(변경감지)을 수행하지 않아서 성능 향상이 가능
        public ItemFormDto getItemDtl(Long itemId){

            List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);// 해당 상품의 이미지를 조회 등록순으로 가지고 오기 위해
                                                                    //  상품 이미지 아이디 오름차순으로 가지고 온다.
            List<ItemImgDto> itemImgDtoList = new ArrayList<>();
            for(ItemImg itemImg : itemImgList){ // 조회한 ItemImg 엔티티를 ItemImgDto 객체로 만들어서 리스트에 추가한다.
                ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
                itemImgDtoList.add(itemImgDto);
            }
            Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
                                // 상품의 아이디를 통해 상품 엔티티를 조회한다 이때 존재하지 않으면 예외처리를 발생한다.
            ItemFormDto itemFormDto = ItemFormDto.of(item);
            itemFormDto.setItemImgDtoList(itemImgDtoList);
            return itemFormDto;
        }

    //상품 업데이트시 변경 감지
    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{
        // 상품 등록 화면으로부터 전달 받은 상품 아이디를 이용하여 상품 엔티티를 조회한다.
        Item item = itemRepository.findById(itemFormDto.getId()).orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto); // 상품 등록 화면으로부터 전달 받은 ItemFormDto를 통해 상품 엔티티를 업데이트 한다.

        List<Long> itemImgIds = itemFormDto.getItemImgIds(); // 상품 이미지 아이디 리스트를 조회한다.

        for(int i=0 ; i<itemImgFileList.size() ; i++){
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
            // 상품 이미지를 업데이트 하기 위해서 updateItemImg 메서드에 상품 이미지 아이디와, 상품 이미지 파일 정보를 파라미터로 전달한다.
        }
        return item.getId();
    }

    // 상품 조회 조건과 페이지 정보를 파라미터로 받아서 상품 데이터를 조회하는 getAdminItemPage 메서드
    @Transactional(readOnly = true) // 데이터 수정이 없기 때문에 최적화를 위해 설정
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }


    }

