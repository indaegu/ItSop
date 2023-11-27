package kosta.main.bids.service;

import kosta.main.bids.dto.BidsDto;
import kosta.main.bids.dto.BidResponseDTO;
import kosta.main.bids.dto.BidListDTO;
import kosta.main.bids.entity.Bid;
import kosta.main.bids.repository.BidRepository;
import kosta.main.exchangeposts.entity.ExchangePost;
import kosta.main.exchangeposts.repository.ExchangePostsRepository;
import kosta.main.items.entity.Item;
import kosta.main.items.repository.ItemsRepository;
import kosta.main.users.entity.User;
import kosta.main.users.repository.UsersRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BidService {

    private final BidRepository bidRepository;
    private final ExchangePostsRepository exchangePostsRepository;
    private final UsersRepository usersRepository;
    private final ItemsRepository itemsRepository;

    @Transactional
    public BidResponseDTO createBid(BidsDto bidDTO) {
        ExchangePost exchangePost = exchangePostsRepository.findById(bidDTO.getExchangePostId())
            .orElseThrow(() -> new RuntimeException("ExchangePost not found"));
        User bidder = usersRepository.findById(bidDTO.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (exchangePost.getUser().equals(bidder)) { // 본인의 글에는 입찰 불가
            throw new RuntimeException("Post owner cannot bid on their own post");
        }

        List<Item> items = itemsRepository.findAllById(bidDTO.getItemIds());
        for (Item item : items) { // 입찰에 사용중인 아이템은 사용불가
            if (item.getIsBiding() == Item.IsBiding.BIDING) {
                throw new RuntimeException("Item is already in bidding");
            }
            item.setIsBiding(Item.IsBiding.BIDING);
            itemsRepository.save(item);
        }

        Bid bid = Bid.builder()
            .user(bidder)
            .exchangePost(exchangePost)
            .status(Bid.BidStatus.BIDDING) // 기본값이 BIDDING이므로 이를 생략할 수도 있습니다.
            .items(items)
            .build();
        bidRepository.save(bid);

        return BidResponseDTO.builder()
            .bidId(bid.getBidId())
            .exchangePostId(exchangePost.getExchangePostId())
            // ... 다른 필드 설정
            .build();
    }

    @Transactional(readOnly = true)
    public List<BidListDTO> findAllBids() {
        return bidRepository.findAll().stream()
            .map(bid -> BidListDTO.builder()
                .bidId(bid.getBidId())
                // ... 요약 정보 필드 설정
                .build())
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BidResponseDTO findBidById(Integer bidId) {
        Bid bid = bidRepository.findById(bidId)
            .orElseThrow(() -> new RuntimeException("Bid not found"));

        return BidResponseDTO.builder()
            .bidId(bid.getBidId())
            // ... 필드 설정
            .build();
    }

    @Transactional
    public BidResponseDTO updateBid(Integer bidId, BidsDto bidDTO) {
        Bid existingBid = bidRepository.findById(bidId)
            .orElseThrow(() -> new RuntimeException("Bid not found"));

        // 사용자 업데이트 (예: 입찰자 변경)
        User bidder = usersRepository.findById(bidDTO.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        existingBid.setUser(bidder);

        // 입찰 상태 업데이트
        existingBid.setStatus(bidDTO.getStatus());

        // 아이템 목록 업데이트
        List<Item> items = itemsRepository.findAllById(bidDTO.getItemIds());
        existingBid.setItems(items);

        // 기존에 연결된 아이템의 상태를 NOT_BIDING으로 변경
        // (이 부분은 비즈니스 로직에 따라 달라질 수 있습니다)
        existingBid.getItems().forEach(item -> {
            item.setIsBiding(Item.IsBiding.NOT_BIDING);
            itemsRepository.save(item);
        });

        // 새로운 아이템의 상태를 BIDING으로 변경
        items.forEach(item -> {
            item.setIsBiding(Item.IsBiding.BIDING);
            itemsRepository.save(item);
        });

        bidRepository.save(existingBid);

        return BidResponseDTO.builder()
            .bidId(existingBid.getBidId())
            .userId(bidder.getUserId())
            .exchangePostId(existingBid.getExchangePost().getExchangePostId())
            .status(existingBid.getStatus())
            .itemIds(items.stream().map(Item::getItemId).collect(Collectors.toList()))
            .build();
    }
    @Transactional(readOnly = true)
    public List<BidListDTO> findAllBidsForPost(Integer exchangePostId) {
        ExchangePost exchangePost = exchangePostsRepository.findById(exchangePostId)
            .orElseThrow(() -> new RuntimeException("ExchangePost not found"));

        return bidRepository.findByExchangePost(exchangePost).stream()
            .map(bid -> BidListDTO.builder()
                .bidId(bid.getBidId())
                .userId(bid.getUser().getUserId())
                .exchangePostId(bid.getExchangePost().getExchangePostId())
                .status(bid.getStatus())
                .itemIds(bid.getItems().stream().map(Item::getItemId).collect(Collectors.toList()))
                .build())
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteBid(Integer bidId) {
        Bid bid = bidRepository.findById(bidId)
            .orElseThrow(() -> new RuntimeException("Bid not found"));

        for (Item item : bid.getItems()) {
            item.setIsBiding(Item.IsBiding.NOT_BIDING);
            itemsRepository.save(item);
        }

        bidRepository.delete(bid);
    }

    // 추가적인 메서드, 예: 입찰 상태 변경 메서드
}
